/*
 * Copyright 2023-2026 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jfx.canvas;

import com.mammb.code.canvas.lib.lib_h;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.ref.Cleaner;

/**
 * The NativeGlobal.
 * @author Naotsugu Kobayashi
 */
final class NativeGlobal {

    private static final Cleaner cleaner = Cleaner.create();
    private static final NativeGlobal instance = new NativeGlobal();

    private NativeGlobal() {
        // init native library
        NativeLibraryLoader.loadByName("lib");
        // hook release to the native shared resources
        Runtime.getRuntime().addShutdownHook(new Thread(lib_h::release_shared_resources));
    }

    public static NativeGlobal instance() {
        return instance;
    }

    public Cleaner.Cleanable cleaner(GraphicsContext gc, MemorySegment ctxSegment, Arena arena) {
        return cleaner.register(gc, new CleanerState(gc.getCanvas().getRenderPulse(), ctxSegment, arena));
    }

    record CleanerState(RenderPulse renderPulse, MemorySegment ctxSegment, Arena arena) implements Runnable {
        @Override
        public void run() {
            renderPulse.stop();
            if (ctxSegment != null && !ctxSegment.equals(MemorySegment.NULL)) {
                lib_h.destroy_render_context(ctxSegment);
            }
            if (arena != null) arena.close();
        }
    }


}
