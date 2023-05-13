package com.mammb.code.editor2.model;

public record PointText(OffsetPoint point, String text) {

    public int tailOffset() {
        return point.offset() + text.length();
    }

    public int tailCpOffset() {
        return point.cpOffset() + Character.codePointCount(text, 0, text.length());
    }

}
