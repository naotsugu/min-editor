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
package com.mammb.code.editor.ui.pane.impl;

import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.ui.pane.Global;
import com.mammb.code.editor.ui.pane.Rect;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Draw utilities.
 * @author Naotsugu Kobayashi
 */
public class Draws {

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
                : Global.numberCharacterWidth(gc.getFont());
            var rect = new Rect(x, top, w, lineHeight).smaller(0.8);

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

}
