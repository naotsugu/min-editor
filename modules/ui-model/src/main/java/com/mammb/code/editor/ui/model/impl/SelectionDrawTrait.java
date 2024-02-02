/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.javafx.layout.FxFonts;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.SelectionDraw;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Objects;

/**
 * SelectionDrawTrait.
 * @author Naotsugu Kobayashi
 */
public interface SelectionDrawTrait extends SelectionDraw {

    /** The color. */
    Color color = new Color(0.6784314F, 0.84705883F, 0.9019608F, 0.3);

    /**
     * Get the min select offset.
     * @return the min select offset
     */
    OffsetPoint min();

    /**
     * Get the max select offset.
     * @return the max select offset
     */
    OffsetPoint max();


    /**
     * Draw the selection.
     * @param gc the graphics context
     * @param run the text run
     * @param offsetY the position y
     * @param left the left position of run(margin included)
     */
    @Override
    default void draw(GraphicsContext gc, TextRun run, double offsetY, double left) {

        final OffsetPoint min = min();
        final OffsetPoint max = max();

        if (min == null || Objects.equals(min, max)) {
            return;
        }

        long runStart = run.offset();
        long runEnd = runStart + run.length();

        if (max().offset() >= runStart && min().offset() < runEnd) {

            final String text = run.text();

            if (runEnd <= max().offset() &&
                ((text.length() == 1 && text.charAt(0) == '\n') ||
                    (text.length() == 2 && text.charAt(0) == '\r' && text.charAt(1) == '\n'))) {

                gc.setFill(color);
                gc.fillRect(run.layout().x() + left, offsetY, FxFonts.uppercaseLetterWidth(gc.getFont()), run.textLine().leadingHeight());

            } else {

                double x1 = run.offsetToX().apply(Math.toIntExact(Math.max(min().offset(), runStart) - runStart));
                double x2 = run.offsetToX().apply(Math.toIntExact(Math.min(max().offset(), runEnd) - runStart));

                gc.setFill(color);
                gc.fillRect(x1 + left, offsetY, x2 - x1, run.textLine().leadingHeight());

            }
        }

    }

}
