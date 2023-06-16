package com.mammb.code.editor.javafx.layout;

import com.mammb.code.editor2.model.layout.FontStyle;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

public class FxFontMetrics implements com.mammb.code.editor2.model.layout.FontMetrics<Font> {

    private final Map<Font, FontMetrics> metrics = new HashMap<>();
    private final Font base;
    private final FontLoader fontLoader;

    public FxFontMetrics(Font font) {
        fontLoader = Toolkit.getToolkit().getFontLoader();
        base = font;
        metrics.put(base, fontLoader.getFontMetrics(font));
    }


    /**
     * Get the base font line height(ascent + descent + leading).
     * @return the base font line height(ascent + descent + leading)
     */
    public double lineHeight() {
        return metrics.get(base).getLineHeight();
    }


    /**
     * Get the font line height(ascent + descent + leading).
     * @param font the font
     * @return the font line height(ascent + descent + leading)
     */
    public double lineHeight(FontStyle<Font, ?> font) {
        return metrics.computeIfAbsent(
                font.font(),
                fontLoader::getFontMetrics)
            .getLineHeight();
    }

}
