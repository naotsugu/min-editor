/*
 * Copyright 2023-2026 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jfx.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;

/**
 * The ContextState.
 * @author Naotsugu Kobayashi
 */
class ContextState {

    Paint fill;
    Paint stroke;
    double linewidth;
    StrokeLineCap linecap;
    StrokeLineJoin linejoin;
    Font font;

    ContextState() {
        init();
    }

    final void init() {
        set(Color.BLACK,
            Color.BLACK,
            1.0,
            StrokeLineCap.SQUARE,
            StrokeLineJoin.MITER,
            Font.getDefault());
    }

    ContextState(ContextState copy) {
        set(copy.fill,
            copy.stroke,
            copy.linewidth,
            copy.linecap,
            copy.linejoin,
            copy.font);
    }

    final void set(
            Paint fill,
            Paint stroke,
            double linewidth,
            StrokeLineCap linecap,
            StrokeLineJoin linejoin,
            Font font) {
        this.fill = fill;
        this.stroke = stroke;
        this.linewidth = linewidth;
        this.linecap = linecap;
        this.linejoin = linejoin;
        this.font = font;
    }

    ContextState copy() {
        return new ContextState(this);
    }

    void restore(GraphicsContext ctx) {
        ctx.setFill(fill);
        ctx.setStroke(stroke);
        ctx.setLineWidth(linewidth);
        ctx.setLineCap(linecap);
        ctx.setLineJoin(linejoin);
        ctx.setFont(font);
    }

}
