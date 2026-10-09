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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.ImageView;

/**
 * The Canvas.
 * @author Naotsugu Kobayashi
 */
public class Canvas extends ImageView {

    private DoubleProperty width = new SimpleDoubleProperty(0);
    private DoubleProperty height = new SimpleDoubleProperty(0);

    private final GraphicsContext ctx;
    private final RenderPulse renderPulse;

    public Canvas(int width, int height) {
        ctx = new GraphicsContext(this, width, height);
        renderPulse = new RenderPulse(ctx::render);
        this.width.set(width);
        this.height.set(height);
        renderPulse.start();
    }

    public GraphicsContext getGraphicsContext() {
        return ctx;
    }

    RenderPulse getRenderPulse() {
        return renderPulse;
    }

    public void setSize(int width, int height) {
        this.width.set(width);
        this.height.set(height);
        ctx.resize(width, height);
    }

    public final void setWidth(double value) {
        width.set(value);
    }

    public final double getWidth() {
        return width.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public final double getHeight() {
        return height.get();
    }

}
