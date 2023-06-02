package com.mammb.code.editor.model.layout;

/**
 * Like a Paragraph in Skia.
 */
public interface ShapedText {

    ShapedText add(String text, String fontName);

    void layout();

    void reset();

    void setWrapWidth(float wrapWidth);

}
