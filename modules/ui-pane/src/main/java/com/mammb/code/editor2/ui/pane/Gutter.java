/*
 * Copyright 2019-2023 the original author or authors.
 *
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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor2.model.layout.TextRun;
import javafx.scene.canvas.GraphicsContext;

/**
 * Gutter.
 * @author Naotsugu Kobayashi
 */
public class Gutter {

    private double width = 30;

    /**
     * Get the width of gutter.
     * @return the width of gutter
     */
    public double width() {
        return width;
    }

    public void draw(GraphicsContext gc, TextRun run, double top, double lineHeight) {
        gc.clearRect(0, top, width - 0.5, lineHeight);
        gc.fillText(run.source().point().row() + 1 + "", 0, top + run.baseline());
    }

}