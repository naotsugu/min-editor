package com.mammb.code.javafx.scene.text.impl;

import com.mammb.code.editor.model.layout.Bounds;
import com.mammb.code.editor.model.layout.EmbedSpan;
import com.sun.javafx.geom.RectBounds;

public class EmbedSpanImpl implements EmbedSpan {

    Bounds bounds;

    public EmbedSpanImpl(BoundsRect boundsRect) {
        this.bounds = boundsRect;
    }

    public static EmbedSpan of(float baseline, float width, float height) {
        return new EmbedSpanImpl(
            BoundsRect.of(new RectBounds(0, -baseline, width, (height - baseline))));
    }

    @Override
    public Bounds figure() {
        return bounds;
    }
}
