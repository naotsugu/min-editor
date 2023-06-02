package com.mammb.code.editor2.model.layout.fx;

/**
 * Like a Paragraph in Skia.
 */
public interface ShapedText {

    ShapedText add(String text, String fontName);

    void layout();

    void reset();

    void setWrapWidth(float wrapWidth);

}
