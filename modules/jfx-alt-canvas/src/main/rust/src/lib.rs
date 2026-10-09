use std::borrow::Cow;
use std::slice;
use std::ffi::{c_char, CStr};
use std::sync::{Arc, Mutex, MutexGuard};
use vello::{
    kurbo::{Affine, BezPath, Join, Cap, Rect, Stroke, Ellipse, RoundedRect, Line},
    peniko::{Color, Fill, StyleRef}, Scene, Glyph};
use vello::wgpu;
use parley::{Alignment, AlignmentOptions, FontContext, FontFamily, Layout,
             LayoutContext, PositionedLayoutItem, StyleProperty};

// -- shared resources --------------------------------------------------------

/// A point returned by value to the caller (two f64, same layout as a C struct).
#[repr(C)]
pub struct Point { x: f64, y: f64, }

/// Text resources shared by all RenderContexts.
/// ranged_builder() needs both contexts as &mut, so they live under one lock.
struct TextResource {
    font_cx: FontContext,
    layout_cx: LayoutContext<()>,
}

/// Heavy resources shared by all RenderContexts.
/// Device and Queue are cheap to clone and thread-safe, so each RenderContext keeps
/// its own clone. Renderer and TextEngine need &mut access, so they are behind Mutexes.
struct SharedResource {
    device: wgpu::Device,
    queue: wgpu::Queue,
    renderer: Mutex<vello::Renderer>,
    text: Mutex<TextResource>,
}
impl SharedResource {
    fn new() -> SharedResource {

        // initialize wgpu device and queue for GPU rendering
        let instance = wgpu::Instance::default();
        let adapter = pollster::block_on(instance.request_adapter(&wgpu::RequestAdapterOptions {
            power_preference: wgpu::PowerPreference::default(),
            force_fallback_adapter: false,
            compatible_surface: None,
            ..Default::default()
        }))
        .expect("failed to find an appropriate adapter");
        let (device, queue) = pollster::block_on(adapter.request_device(&wgpu::DeviceDescriptor {
            label: Some("Device"),
            required_features: wgpu::Features::empty(),
            ..Default::default()
        }))
        .expect("failed to create device");

        // initialize renderer
        let renderer = vello::Renderer::new(
            &device,
            vello::RendererOptions {
                use_cpu: false,
                antialiasing_support: vello::AaSupport::all(),
                num_init_threads: None,
                pipeline_cache: None,
            },
        ).expect("failed to create renderer");

        SharedResource {
            device,
            queue,
            renderer: Mutex::new(renderer),
            text: Mutex::new(TextResource {
                font_cx: FontContext::new(),
                layout_cx: LayoutContext::new(),
            }),
        }

    }
}

/// Global slot for the shared resources. It is filled lazily by the first
/// create_render_context() and emptied by release_shared_resources().
static SHARED: Mutex<Option<Arc<SharedResource>>> = Mutex::new(None);

/// Locks a mutex and ignores poisoning, so a panic in one call
/// does not break every later call across the FFI boundary.
fn lock<T>(m: &Mutex<T>) -> MutexGuard<'_, T> {
    m.lock().unwrap_or_else(|e| e.into_inner())
}

/// Returns the shared resources, creating them on first use.
fn acquire_shared() -> Arc<SharedResource> {
    let mut slot = lock(&SHARED);
    if let Some(shared) = slot.as_ref() {
        return Arc::clone(shared);
    }
    let shared = Arc::new(SharedResource::new());
    *slot = Some(Arc::clone(&shared));
    shared
}

/// Drops the global reference to the shared resources (GPU device, renderer, font caches).
/// Existing RenderContexts keep working: they hold their own reference, and the resources
/// are actually freed when the last of them is destroyed.
/// A RenderContext created after this call gets a fresh set of shared resources.
/// Call this once at shutdown, after destroying all RenderContexts, to free everything.
#[unsafe(no_mangle)]
pub extern "C" fn release_shared_resources() {
    let released = lock(&SHARED).take();
    if released.is_some() {
        println!("Shared resources released.");
    }
}

// -- context -----------------------------------------------------------------

/// RenderContext
pub struct RenderContext {
    device: wgpu::Device,
    queue: wgpu::Queue,
    texture: wgpu::Texture,
    readback_buffer: wgpu::Buffer,
    width: u32,
    height: u32,
    scene: Scene,
    fill_color: Color,
    stroke_color: Color,
    line_width: f64,
    line_cap: Cap,
    line_join: Join,
    font_family: String,
    font_size: f32,
    // declared last so it is dropped after the GPU resources above
    shared: Arc<SharedResource>,
}
impl RenderContext {
    /// Builds the Stroke from the current line width and cap, join.
    fn stroke_style(&self) -> Stroke {
        Stroke::new(self.line_width).with_caps(self.line_cap).with_join(self.line_join)
    }
}

#[unsafe(no_mangle)]
pub extern "C" fn create_render_context(width: u32, height: u32) -> *mut RenderContext {

    let shared = acquire_shared();
    let device = shared.device.clone();
    let queue = shared.queue.clone();

    // create a render target texture and a readback buffer
    let (texture, readback_buffer) = create_render_target(&device, width, height);

    let ctx = RenderContext {
        device,
        queue,
        texture,
        readback_buffer,
        width,
        height,
        scene: Scene::new(),
        fill_color: Color::BLACK,
        stroke_color: Color::BLACK,
        line_width: 1.,
        line_cap: Cap::Square,
        line_join: Join::Miter,
        font_family: "sans-serif".to_string(),
        font_size: 14.0,
        shared,
    };

    // detach from Rust's memory management and pass to Java as a raw pointer
    Box::into_raw(Box::new(ctx))
}

#[unsafe(no_mangle)]
pub extern "C" fn destroy_render_context(ctx_ptr: *mut RenderContext) {
    if !ctx_ptr.is_null() {
        // reconstruct the Box from the raw pointer to trigger
        // automatic dropping (memory deallocation) when going out of scope
        unsafe { let _ = Box::from_raw(ctx_ptr); }
        println!("RenderContext destroyed.");
    }
}

#[unsafe(no_mangle)]
pub extern "C" fn render(ctx_ptr: *mut RenderContext, buffer: *mut u8) {

    if ctx_ptr.is_null() || buffer.is_null() {
        return;
    }

    // restore the reference from the raw pointer (without taking ownership)
    let ctx = unsafe { &mut *ctx_ptr };
    let (device, queue, texture, readback_buffer, width, height) = (
        &ctx.device,
        &ctx.queue,
        &ctx.texture,
        &ctx.readback_buffer,
        ctx.width,
        ctx.height,
    );

    // draw on the GPU; the shared renderer is locked only while the scene is encoded
    {
        let mut renderer = lock(&ctx.shared.renderer);
        renderer.render_to_texture(
            device,
            queue,
            &ctx.scene,
            &texture.create_view(&wgpu::TextureViewDescriptor::default()),
            &vello::RenderParams {
                base_color: Color::TRANSPARENT,
                width, height,
                antialiasing_method: vello::AaConfig::Area,
            },
        ).expect("failed to render texture");
    }

    let row = row_bytes(width);
    let mut encoder = device.create_command_encoder(
        &wgpu::CommandEncoderDescriptor::default());
    encoder.copy_texture_to_buffer(
        wgpu::TexelCopyTextureInfo {
            texture,
            mip_level: 0,
            origin: wgpu::Origin3d::ZERO,
            aspect: wgpu::TextureAspect::All,
        },
        wgpu::TexelCopyBufferInfo {
            buffer: readback_buffer,
            layout: wgpu::TexelCopyBufferLayout {
                offset: 0,
                bytes_per_row: Some(row),
                rows_per_image: Some(height),
            },
        },
        wgpu::Extent3d {
            width, height, depth_or_array_layers: 1
        },
    );
    queue.submit(Some(encoder.finish()));

    let buffer_slice = readback_buffer.slice(..);
    let (tx, rx) = std::sync::mpsc::channel();
    buffer_slice.map_async(wgpu::MapMode::Read, move |v| tx.send(v).unwrap());

    device.poll(wgpu::PollType::wait_indefinitely()).unwrap();
    rx.recv().unwrap().unwrap();
    let mapped_data = buffer_slice.get_mapped_range().unwrap();

    // SIMD copy (RGBA -> BGRA)
    let src_bytes = unsafe { slice::from_raw_parts(mapped_data.as_ptr(), mapped_data.len()) };
    let dst_bytes = unsafe { slice::from_raw_parts_mut(buffer, mapped_data.len()) };
    for (src, dst) in src_bytes.chunks_exact(4).zip(dst_bytes.chunks_exact_mut(4)) {
        dst[0] = src[2]; // B <- R
        dst[1] = src[1]; // G <- G
        dst[2] = src[0]; // R <- B
        dst[3] = src[3]; // A <- A
    }

    drop(mapped_data);
    readback_buffer.unmap();
    ctx.scene.reset();
}

/// Resizes the canvas. The size must be non-zero and within the device limits.
/// Returns false if the size is invalid; the old size is kept in that case.
/// Call this before drawing a frame: commands already added to the scene are kept as they are.
/// After resizing, the buffer passed to render() must hold width * height * 4 bytes.
#[unsafe(no_mangle)]
pub extern "C" fn resize(ctx_ptr: *mut RenderContext, width: u32, height: u32) -> bool {
    if ctx_ptr.is_null() || width == 0 || height == 0 { return false; }
    let ctx = unsafe { &mut *ctx_ptr };
    if width == ctx.width && height == ctx.height { return true; }

    let max = ctx.device.limits().max_texture_dimension_2d;
    if width > max || height > max { return false; }

    // the old texture and buffer are dropped when replaced
    let (texture, readback_buffer) = create_render_target(&ctx.device, width, height);
    ctx.texture = texture;
    ctx.readback_buffer = readback_buffer;
    ctx.width = width;
    ctx.height = height;
    true
}

// -- attribute ---------------------------------------------------------------

/// Sets the current fill paint attribute. The default value is BLACK.
#[unsafe(no_mangle)]
pub extern "C" fn set_fill(ctx_ptr: *mut RenderContext, r: u8, g: u8, b: u8, a: u8) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.fill_color = Color::from_rgba8(r, g, b, a);
}

/// Sets the current stroke paint attribute. The default value is BLACK.
#[unsafe(no_mangle)]
pub extern "C" fn set_stroke(ctx_ptr: *mut RenderContext, r: u8, g: u8, b: u8, a: u8) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.stroke_color = Color::from_rgba8(r, g, b, a);
}

/// Sets the width; it applies to all following stroke operations
#[unsafe(no_mangle)]
pub extern "C" fn set_line_width(ctx_ptr: *mut RenderContext, line_width: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.line_width = line_width;
}

/// Sets the line cap: 0 = butt, 1 = round, 2 = square.
/// Unknown values are ignored.
#[unsafe(no_mangle)]
pub extern "C" fn set_line_cap(ctx_ptr: *mut RenderContext, cap: u32) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.line_cap = match cap {
        0 => Cap::Butt,
        1 => Cap::Round,
        2 => Cap::Square,
        _ => return,
    };
}

/// Sets the line join: 0 = Bevel, 1 = round, 2 = Miter.
/// Unknown values are ignored.
#[unsafe(no_mangle)]
pub extern "C" fn set_line_join(ctx_ptr: *mut RenderContext, join: u32) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.line_join = match join {
        0 => Join::Bevel,
        1 => Join::Round,
        2 => Join::Miter,
        _ => return,
    };
}

/// Sets the font families as a comma-separated list, e.g. "Segoe UI, Meiryo, sans-serif".
/// Characters missing in a family fall back to the next one, then to other system fonts.
/// Returns false if none of the listed (non-generic) families is installed;
/// the list is stored anyway.
#[unsafe(no_mangle)]
pub extern "C" fn set_font_family(ctx_ptr: *mut RenderContext, names_ptr: *const c_char) -> bool {
    if ctx_ptr.is_null() || names_ptr.is_null() { return false; }
    let ctx = unsafe { &mut *ctx_ptr };
    let c_str = unsafe { CStr::from_ptr(names_ptr) };
    let names = match c_str.to_str() {
        Ok(s) => s,
        Err(_) => return false, // illegal UTF-8
    };
    ctx.font_family = names.trim().to_string();

    // the font collection is shared, so it must be locked while it is queried
    let mut text = lock(&ctx.shared.text);

    // generic names (serif, sans-serif, ...) are resolved by the system and always count as found
    names.split(',')
        .map(|n| n.trim().trim_matches(|c| c == '"' || c == '\''))
        .filter(|n| !n.is_empty())
        .any(|n| {
            matches!(
                n.to_ascii_lowercase().as_str(),
                "serif" | "sans-serif" | "monospace" | "cursive" | "fantasy" | "system-ui"
            ) || text.font_cx.collection.family_id(n).is_some()
        })
}

/// Sets the font size in pixels. Non-positive or non-finite values are ignored.
#[unsafe(no_mangle)]
pub extern "C" fn set_font_size(ctx_ptr: *mut RenderContext, size: f64) {
    if ctx_ptr.is_null() || !size.is_finite() || size <= 0.0 { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.font_size = size as f32;
}

// -- measure -----------------------------------------------------------------

/// Returns the advance width of a single Unicode code point in the current font.
/// Returns 0.0 for an invalid code point (surrogates, > 0x10FFFF) or a null context.
#[unsafe(no_mangle)]
pub extern "C" fn get_advance(ctx_ptr: *mut RenderContext, code_point: u32) -> f64 {
    if ctx_ptr.is_null() { return 0.0; }
    let ctx = unsafe { &*ctx_ptr };
    let Some(ch) = char::from_u32(code_point) else { return 0.0; };
    let mut buf = [0u8; 4];
    measure_width(ctx, ch.encode_utf8(&mut buf))
}

/// Returns the advance width of a NUL-terminated UTF-8 string in the current font.
/// The string is measured as a single line, so it is the same width fill_text() draws.
/// Returns 0.0 for illegal UTF-8, an empty string or a null pointer.
#[unsafe(no_mangle)]
pub extern "C" fn get_text_advance(ctx_ptr: *mut RenderContext, text_ptr: *const c_char) -> f64 {
    if ctx_ptr.is_null() || text_ptr.is_null() { return 0.0; }
    let ctx = unsafe { &*ctx_ptr };
    let c_str = unsafe { CStr::from_ptr(text_ptr) };
    let Ok(text) = c_str.to_str() else { return 0.0; };
    measure_width(ctx, text)
}

/// Returns the line height of the current font: ascent + descent + leading.
/// A reference character is laid out, so the value follows the first available family
/// in the font list. A line containing fallback fonts may be taller when drawn.
#[unsafe(no_mangle)]
pub extern "C" fn get_line_height(ctx_ptr: *mut RenderContext) -> f64 {
    if ctx_ptr.is_null() { return 0.0; }
    let ctx = unsafe { &*ctx_ptr };
    let layout = build_layout(ctx, "M");
    let Some(line) = layout.lines().next() else { return 0.0; };
    line.metrics().line_height as f64
}

// -- draw --------------------------------------------------------------------

/// Fills a rectangle using the current fill paint.
#[unsafe(no_mangle)]
pub extern "C" fn fill_rect(ctx_ptr: *mut RenderContext,
        x: f64, y: f64, width: f64, height: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let (scene, fill_color) = (&mut ctx.scene, ctx.fill_color);
    scene.fill(
        Fill::NonZero,
        Affine::IDENTITY,
        fill_color,
        None,
        &Rect::new(x, y, x + width, y + height),
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn stroke_rect(ctx_ptr: *mut RenderContext,
        x: f64, y: f64, width: f64, height: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let stroke = ctx.stroke_style();
    let (scene, stroke_color) = (&mut ctx.scene, ctx.stroke_color);
    scene.stroke(
        &stroke,
        Affine::IDENTITY,
        stroke_color,
        None,
        &Rect::new(x, y, x + width, y + height),
    );
}

/// Fills an oval inscribed in the given bounding rectangle using the current fill paint.
#[unsafe(no_mangle)]
pub extern "C" fn fill_oval(ctx_ptr: *mut RenderContext,
        x: f64, y: f64, width: f64, height: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let (scene, fill_color) = (&mut ctx.scene, ctx.fill_color);
    // center is the middle of the bounding box, radii are half of its size
    let oval = Ellipse::new((x + width / 2.0, y + height / 2.0), (width / 2.0, height / 2.0), 0.0);
    scene.fill(
        Fill::NonZero,
        Affine::IDENTITY,
        fill_color,
        None,
        &oval,
    );
}

/// Strokes an oval inscribed in the given bounding rectangle using the current stroke paint and width.
#[unsafe(no_mangle)]
pub extern "C" fn stroke_oval(ctx_ptr: *mut RenderContext,
        x: f64, y: f64, width: f64, height: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let stroke = ctx.stroke_style();
    let (scene, stroke_color) = (&mut ctx.scene, ctx.stroke_color);
    // center is the middle of the bounding box, radii are half of its size
    let oval = Ellipse::new((x + width / 2.0, y + height / 2.0), (width / 2.0, height / 2.0), 0.0);
    scene.stroke(
        &stroke,
        Affine::IDENTITY,
        stroke_color,
        None,
        &oval,
    );
}

/// Fills a rounded rectangle using the current fill paint.
/// radius is the radius of the corner arcs.
#[unsafe(no_mangle)]
pub extern "C" fn fill_round_rect(ctx_ptr: *mut RenderContext,
        x: f64, y: f64, width: f64, height: f64, radius: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let (scene, fill_color) = (&mut ctx.scene, ctx.fill_color);
    scene.fill(
        Fill::NonZero,
        Affine::IDENTITY,
        fill_color,
        None,
        &RoundedRect::new(x, y, x + width, y + height, radius),
    );
}

/// Strokes a rounded rectangle using the current stroke paint and width.
/// radius is the radius of the corner arcs.
#[unsafe(no_mangle)]
pub extern "C" fn stroke_round_rect(ctx_ptr: *mut RenderContext,
        x: f64, y: f64, width: f64, height: f64, radius: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let stroke = ctx.stroke_style();
    let (scene, stroke_color) = (&mut ctx.scene, ctx.stroke_color);
    scene.stroke(
        &stroke,
        Affine::IDENTITY,
        stroke_color,
        None,
        &RoundedRect::new(x, y, x + width, y + height, radius),
    );
}

/// Strokes a line from (x1, y1) to (x2, y2) using the current stroke paint and width.
#[unsafe(no_mangle)]
pub extern "C" fn stroke_line(ctx_ptr: *mut RenderContext,
        x1: f64, y1: f64, x2: f64, y2: f64) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let stroke = ctx.stroke_style();
    let (scene, stroke_color) = (&mut ctx.scene, ctx.stroke_color);
    scene.stroke(
        &stroke,
        Affine::IDENTITY,
        stroke_color,
        None,
        &Line::new((x1, y1), (x2, y2)),
    );
}

/// Fills a closed polygon using the current fill paint.
/// The even-odd rule is used, same as Java's fillPolygon.
#[unsafe(no_mangle)]
pub extern "C" fn fill_polygon(ctx_ptr: *mut RenderContext,
        x_points: *const f64, y_points: *const f64, n_points: u32) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let Some(path) = (unsafe { points_to_path(x_points, y_points, n_points, true) }) else { return; };
    let (scene, fill_color) = (&mut ctx.scene, ctx.fill_color);
    scene.fill(
        Fill::EvenOdd,
        Affine::IDENTITY,
        fill_color,
        None,
        &path,
    );
}

/// Strokes a closed polygon using the current stroke paint, width and cap.
#[unsafe(no_mangle)]
pub extern "C" fn stroke_polygon(ctx_ptr: *mut RenderContext,
        x_points: *const f64, y_points: *const f64, n_points: u32) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let Some(path) = (unsafe { points_to_path(x_points, y_points, n_points, true) }) else { return; };
    let stroke = ctx.stroke_style(); // build before borrowing scene mutably
    let (scene, stroke_color) = (&mut ctx.scene, ctx.stroke_color);
    scene.stroke(
        &stroke,
        Affine::IDENTITY,
        stroke_color,
        None,
        &path,
    );
}

/// Strokes an open polyline (the last point is not connected to the first one)
/// using the current stroke paint, width and cap.
#[unsafe(no_mangle)]
pub extern "C" fn stroke_polyline(ctx_ptr: *mut RenderContext,
        x_points: *const f64, y_points: *const f64, n_points: u32) {
    if ctx_ptr.is_null() { return; }
    let ctx = unsafe { &mut *ctx_ptr };
    let Some(path) = (unsafe { points_to_path(x_points, y_points, n_points, false) }) else { return; };
    let stroke = ctx.stroke_style(); // build before borrowing scene mutably
    let (scene, stroke_color) = (&mut ctx.scene, ctx.stroke_color);
    scene.stroke(
        &stroke,
        Affine::IDENTITY,
        stroke_color,
        None,
        &path,
    );
}

/// Draws UTF-8 text filled with the current fill paint.
/// (x, y) is the left end of the baseline of the first line.
/// Returns the bottom-right corner of the drawn text.
/// If nothing is drawn (null pointer, illegal UTF-8, empty text), (x, y) is returned.
#[unsafe(no_mangle)]
pub extern "C" fn fill_text(ctx_ptr: *mut RenderContext,
        text_ptr: *const c_char, x: f64, y: f64) -> Point {
    if ctx_ptr.is_null() || text_ptr.is_null() { return Point { x, y }; }
    let c_str = unsafe { CStr::from_ptr(text_ptr) };
    let text = match c_str.to_str() {
        Ok(s) => s,
        Err(_) => return Point { x, y }, // illegal UTF-8
    };
    draw_text_internal(ctx_ptr, text, x, y, false, f64::INFINITY)
}

/// Draws UTF-8 text filled with the current fill paint, like fill_text().
/// If the text is wider than max_width, it is scaled horizontally to fit
/// (the left end and the baseline stay at (x, y)).
/// Nothing is drawn if max_width is not positive or is NaN.
/// Returns the bottom-right corner of the drawn text (after scaling).
#[unsafe(no_mangle)]
pub extern "C" fn fill_text_max_width(ctx_ptr: *mut RenderContext,
        text_ptr: *const c_char, x: f64, y: f64, max_width: f64) -> Point {
    if ctx_ptr.is_null() || text_ptr.is_null() { return Point { x, y }; }
    let c_str = unsafe { CStr::from_ptr(text_ptr) };
    let text = match c_str.to_str() {
        Ok(s) => s,
        Err(_) => return Point { x, y }, // illegal UTF-8
    };
    draw_text_internal(ctx_ptr, text, x, y, false, max_width)
}

/// Draws the outline of UTF-8 text using the current stroke paint and width.
/// Cap and join are also applied, same as the other stroke operations.
/// (x, y) is the left end of the baseline of the first line.
/// Returns the bottom-right corner of the text layout (the stroke width is not included),
/// so it is the same point fill_text() returns for the same text.
/// If nothing is drawn (null pointer, illegal UTF-8, empty text), (x, y) is returned.
#[unsafe(no_mangle)]
pub extern "C" fn stroke_text(ctx_ptr: *mut RenderContext,
        text_ptr: *const c_char, x: f64, y: f64) -> Point {
    if ctx_ptr.is_null() || text_ptr.is_null() { return Point { x, y }; }
    let c_str = unsafe { CStr::from_ptr(text_ptr) };
    let text = match c_str.to_str() {
        Ok(s) => s,
        Err(_) => return Point { x, y }, // illegal UTF-8
    };
    draw_text_internal(ctx_ptr, text, x, y, true, f64::INFINITY)
}

/// Draws the outline of UTF-8 text, like stroke_text().
/// If the text is wider than max_width, it is scaled horizontally to fit
/// (the left end and the baseline stay at (x, y)).
/// The scaling is applied to the stroke too, so the line gets thinner horizontally.
/// Nothing is drawn if max_width is not positive or is NaN.
/// Returns the bottom-right corner of the text layout (after scaling).
#[unsafe(no_mangle)]
pub extern "C" fn stroke_text_max_width(ctx_ptr: *mut RenderContext,
        text_ptr: *const c_char, x: f64, y: f64, max_width: f64) -> Point {
    if ctx_ptr.is_null() || text_ptr.is_null() { return Point { x, y }; }
    let c_str = unsafe { CStr::from_ptr(text_ptr) };
    let text = match c_str.to_str() {
        Ok(s) => s,
        Err(_) => return Point { x, y }, // illegal UTF-8
    };
    draw_text_internal(ctx_ptr, text, x, y, true, max_width)
}
// -- private -----------------------------------------------------------------

/// Builds a path from separate x / y coordinate arrays.
/// Returns None if a pointer is null or there are fewer than 2 points.
/// The caller must guarantee that both pointers are valid for n_points elements.
unsafe fn points_to_path(
    x_points: *const f64, y_points: *const f64, n_points: u32, close: bool) -> Option<BezPath> {
    if x_points.is_null() || y_points.is_null() || n_points < 2 {
        return None;
    }
    let n = n_points as usize;
    let xs = unsafe { slice::from_raw_parts(x_points, n) };
    let ys = unsafe { slice::from_raw_parts(y_points, n) };

    let mut path = BezPath::new();
    path.move_to((xs[0], ys[0]));
    for i in 1..n {
        path.line_to((xs[i], ys[i]));
    }
    if close {
        // connect the last point back to the first one
        path.close_path();
    }
    Some(path)
}

/// Builds a single-line layout of the text with the current font settings.
fn build_layout(ctx: &RenderContext, text: &str) -> Layout<()> {
    // the shared text engine is locked only while the layout is built;
    // the finished layout owns its data and no longer borrows the contexts
    let mut layout: Layout<()> = {
        let mut guard = lock(&ctx.shared.text);
        let TextResource { font_cx, layout_cx } = &mut *guard;
        let mut builder = layout_cx.ranged_builder(font_cx, text, 1.0, true);
        builder.push_default(StyleProperty::FontSize(ctx.font_size));
        builder.push_default(StyleProperty::FontFamily(FontFamily::Source(
            Cow::Borrowed(ctx.font_family.as_str()),
        )));
        let layout = builder.build(text);
        layout
    };
    layout.break_all_lines(None); // no wrapping
    layout.align(Alignment::Start, AlignmentOptions::default());
    layout
}

/// Returns the width of the text laid out as a single line.
/// Trailing whitespace is included, same as the value fill_text() uses for its end point.
fn measure_width(ctx: &RenderContext, text: &str) -> f64 {
    if text.is_empty() { return 0.0; }
    build_layout(ctx, text).full_width() as f64
}

/// Lays out the text and draws its glyphs, filled or stroked.
/// The fill case uses the fill paint; the stroke case uses the stroke paint,
/// width, cap and join.
/// If the layout is wider than max_width, it is scaled horizontally around (x, y).
/// Pass f64::INFINITY for no limit.
fn draw_text_internal(ctx_ptr: *mut RenderContext, text: &str, x: f64, y: f64,
        stroke: bool, max_width: f64) -> Point {

    if ctx_ptr.is_null() { return Point { x, y }; }
    // rejects zero, negative and NaN (all comparisons with NaN are false)
    if !(max_width > 0.0) { return Point { x, y }; }
    let ctx = unsafe { &mut *ctx_ptr };

    // choose paint and style; the stroke is built before borrowing the scene mutably
    let stroke_style = ctx.stroke_style();
    let (color, style): (Color, StyleRef) = if stroke {
        (ctx.stroke_color, StyleRef::from(&stroke_style))
    } else {
        (ctx.fill_color, StyleRef::from(Fill::NonZero))
    };
    let layout = build_layout(ctx, text);

    // place the baseline of the first line at y
    let Some(first_line) = layout.lines().next() else { return Point { x, y }; };
    let origin_y = y as f32 - first_line.metrics().baseline;

    // horizontal scale to fit max_width; 1.0 when the text already fits
    let width = layout.full_width() as f64;
    let scale = if width > max_width { max_width / width } else { 1.0 };
    // scale around (x, y) so that the left end and the baseline do not move
    let transform = Affine::translate((x, y))
        * Affine::scale_non_uniform(scale, 1.0)
        * Affine::translate((-x, -y));

    let scene = &mut ctx.scene;
    for line in layout.lines() {
        for item in line.items() {
            let PositionedLayoutItem::GlyphRun(glyph_run) = item else { continue; };
            let run = glyph_run.run();
            let baseline = origin_y + glyph_run.baseline();
            let mut cursor = x as f32 + glyph_run.offset();
            scene
                .draw_glyphs(run.font())
                .font_size(run.font_size())
                .transform(transform)
                // hinting assumes unscaled glyphs, so it is turned off when squeezed
                .hint(scale == 1.0)
                .normalized_coords(run.normalized_coords())
                .brush(color)
                .draw(style, glyph_run.glyphs().map(|g| {
                    // g.x / g.y are offsets inside the run; y points up in parley
                    let glyph = Glyph { id: g.id, x: cursor + g.x, y: baseline - g.y };
                    cursor += g.advance;
                    glyph
                }));
        }
    }

    // bottom-right corner: full_width() includes trailing whitespace, so the caller
    // can continue drawing right after the text; the width is the scaled one
    Point {
        x: x + width * scale,
        y: (origin_y + layout.height()) as f64,
    }
}

/// Returns the byte length of one row. wgpu requires bytes_per_row of a
/// texture-to-buffer copy to be a multiple of 256, so the caller must pass a
/// width that satisfies this (width * 4 % 256 == 0, i.e. width % 64 == 0).
fn row_bytes(width: u32) -> u32 {
    let bytes = width * 4;
    assert_eq!(bytes % wgpu::COPY_BYTES_PER_ROW_ALIGNMENT, 0,
               "width ({width}) * 4 must be a multiple of {} bytes",
               wgpu::COPY_BYTES_PER_ROW_ALIGNMENT);
    bytes
}

/// Creates the render target texture and the readback buffer for the given size.
fn create_render_target(device: &wgpu::Device, width: u32, height: u32)
        -> (wgpu::Texture, wgpu::Buffer) {

    let row = row_bytes(width);

    let texture = device.create_texture(&wgpu::TextureDescriptor {
        label: Some("Render Target Texture"),
        size: wgpu::Extent3d {
            width,
            height,
            depth_or_array_layers: 1,
        },
        mip_level_count: 1,
        sample_count: 1,
        dimension: wgpu::TextureDimension::D2,
        format: wgpu::TextureFormat::Rgba8Unorm,
        usage: wgpu::TextureUsages::RENDER_ATTACHMENT
             | wgpu::TextureUsages::COPY_SRC
             | wgpu::TextureUsages::STORAGE_BINDING,
        view_formats: &[],
    });

    let readback_buffer = device.create_buffer(&wgpu::BufferDescriptor {
        label: Some("Readback Buffer"),
        // rows are tightly packed, so the size is exactly width * 4 * height
        size: row as wgpu::BufferAddress * height as wgpu::BufferAddress,
        usage: wgpu::BufferUsages::MAP_READ | wgpu::BufferUsages::COPY_DST,
        mapped_at_creation: false,
    });

    (texture, readback_buffer)
}

