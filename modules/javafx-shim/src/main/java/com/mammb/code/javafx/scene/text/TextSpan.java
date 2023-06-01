package com.mammb.code.javafx.scene.text;

import com.mammb.code.editor.model.layout.FontSpan;
import com.mammb.code.javafx.scene.text.impl.TextSpanImpl;
import javafx.scene.text.Font;

public interface TextSpan extends FontSpan<FxFont> {

    static TextSpan of(String text, Font font) {
        return new TextSpanImpl(text, font);
    }
}
