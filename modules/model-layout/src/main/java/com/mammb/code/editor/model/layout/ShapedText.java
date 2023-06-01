package com.mammb.code.editor.model.layout;

/**
 * Like a Paragraph in Skia.
 */
public interface ShapedText {

    ShapedText add(String text, FontFace<?> fontFace);

    ShapedText add(FontSpan<?> span);

    void layout();

    void reset();

}
