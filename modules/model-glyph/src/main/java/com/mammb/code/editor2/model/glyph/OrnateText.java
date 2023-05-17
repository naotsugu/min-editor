package com.mammb.code.editor2.model.glyph;

public record OrnateText(
        String text,
        Font font,
        Color color,
        Decoration decoration) implements Ornate {
}
