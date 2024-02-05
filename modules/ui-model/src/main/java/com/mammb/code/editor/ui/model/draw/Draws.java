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
package com.mammb.code.editor.ui.model.draw;

import com.mammb.code.editor.javafx.layout.FxFonts;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointRange;
import com.mammb.code.editor.ui.model.RectBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Objects;

/**
 * Draw utilities.
 * @author Naotsugu Kobayashi
 */
public class Draws {

    /**
     * Draw the special character.
     * @param gc the graphics context
     * @param run the text run
     * @param top
     * @param lineHeight
     * @param textLeft
     */
    public static void specialCharacter(
            GraphicsContext gc,
            TextRun run,
            double top,
            double lineHeight,
            double textLeft) {

        final String text = run.text();

        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);
            if (ch != '\r' && ch != '\n' && ch != '\t' && ch != '　') {
                continue;
            }

            double x = textLeft + run.offsetToX().apply(i);
            double w = (ch == '　')
                ? textLeft + run.offsetToX().apply(i + 1) - x
                : FxFonts.uppercaseLetterWidth(gc.getFont());
            var rect = new RectBox(x, top, w, lineHeight).smaller(0.8);

            gc.setStroke(Color.GRAY);
            if (ch == '\r') {
                SpecialCharacter.CRLF.draw(gc, rect);
                break;
            } else if (ch == '\n') {
                SpecialCharacter.LF.draw(gc, rect);
                break;
            } else if (ch == '\t') {
                SpecialCharacter.TAB.draw(gc, rect);
            } else {
                SpecialCharacter.WSP.draw(gc, rect);
            }
        }
    }


    /**
     * Draw the selection.
     * @param gc the graphics context
     * @param run the text run
     * @param offsetY the position y
     * @param left the left position of run(margin included)
     * @param range the range of selection
     * @param color the color
     */
    public static void selection(GraphicsContext gc, TextRun run, double offsetY, double left, OffsetPointRange range, Color color) {

        final OffsetPoint min = range.minOffsetPoint();
        final OffsetPoint max = range.maxOffsetPoint();

        if (min == null || Objects.equals(min, max)) {
            return;
        }

        long runStart = run.offset();
        long runEnd = runStart + run.length();

        if (max.offset() >= runStart && min.offset() < runEnd) {

            final String text = run.text();

            if (runEnd <= max.offset() &&
                ((text.length() == 1 && text.charAt(0) == '\n') ||
                    (text.length() == 2 && text.charAt(0) == '\r' && text.charAt(1) == '\n'))) {

                gc.setFill(color);
                gc.fillRect(run.layout().x() + left, offsetY, FxFonts.uppercaseLetterWidth(gc.getFont()), run.textLine().leadingHeight());

            } else {

                double x1 = run.offsetToX().apply(Math.toIntExact(Math.max(min.offset(), runStart) - runStart));
                double x2 = run.offsetToX().apply(Math.toIntExact(Math.min(max.offset(), runEnd) - runStart));

                gc.setFill(color);
                gc.fillRect(x1 + left, offsetY, x2 - x1, run.textLine().leadingHeight());

            }
        }
    }


}
