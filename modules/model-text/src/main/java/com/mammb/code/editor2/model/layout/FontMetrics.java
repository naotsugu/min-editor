package com.mammb.code.editor2.model.layout;

public interface FontMetrics<F> {

    /**
     * Get the base font line height(ascent + descent + leading).
     * @return the base font line height(ascent + descent + leading)
     */
    double lineHeight();

    /**
     * Get the font line height(ascent + descent + leading).
     * @param font the font style
     * @return the font line height(ascent + descent + leading)
     */
    double lineHeight(FontStyle<F, ?> font);

}
