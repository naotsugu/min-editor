package com.mammb.code.javafx.scene.text;

import com.mammb.code.editor.model.layout.LayoutLine;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextLayoutShimTest {

    @Test void setContent() {
        var layout = new TextLayoutShim();
        layout.setWrapWidth(100);
        layout.setContent("abc\n".repeat(5), Font.font(20));

        LayoutLine[] lines = layout.getLines();
        assertEquals(6, lines.length);

        var run = layout.getRuns();
        assertEquals(11, run.length);
    }
}
