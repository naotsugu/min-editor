package com.mammb.code.javafx.scene.text.impl;

import com.mammb.code.editor.model.layout.Bounds;
import com.mammb.code.editor.model.layout.EmbedSpan;

public class EmbedSpanImpl implements EmbedSpan {

    private Bounds bounds;

    public EmbedSpanImpl(RectBounds boundsRect) {
        this.bounds = boundsRect;
    }

    public static EmbedSpan of(float baseline, float width, float height) {
        return new EmbedSpanImpl(
            RectBounds.of(new com.sun.javafx.geom.RectBounds(0, -baseline, width, (height - baseline))));
    }

    @Override
    public Bounds figure() {
        return bounds;
    }
}
