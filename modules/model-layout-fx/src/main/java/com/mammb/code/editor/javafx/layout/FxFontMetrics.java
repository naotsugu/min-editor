package com.mammb.code.editor.javafx.layout;

import com.mammb.code.editor2.model.layout.FontStyle;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class FxFontMetrics implements com.mammb.code.editor2.model.layout.FontMetrics<Font> {

    private final Map<Font, FontMetrics> metrics = new WeakHashMap<>();
    private Font base;
    private final FontLoader fontLoader;

    public FxFontMetrics(Font font) {
        fontLoader = Toolkit.getToolkit().getFontLoader();
        base = Objects.requireNonNull(font);
        metrics.put(base, fontLoader.getFontMetrics(font));
    }


    @Override
    public void setDefault(Font font) {
        base = font;
    }

    @Override
    public double lineHeight() {
        return metrics.get(base).getLineHeight();
    }


    @Override
    public double lineHeight(FontStyle<Font, ?> font) {
        return metrics.computeIfAbsent(
                font.font(),
                fontLoader::getFontMetrics)
            .getLineHeight();
    }

}
