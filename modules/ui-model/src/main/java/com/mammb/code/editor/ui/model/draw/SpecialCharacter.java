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
package com.mammb.code.editor.ui.model.draw;

import com.mammb.code.editor.ui.model.Rect;
import javafx.scene.canvas.GraphicsContext;

/**
 * SpecialCharacter.
 * @author Naotsugu Kobayashi
 */
public interface SpecialCharacter {

    /**
     * Draw
     * @param gc the graphics context
     * @param r the plot area
     */
    void draw(GraphicsContext gc, Rect r);

    /**
     * <pre>
     *     y0  *************
     *         ****** ******        (x1, y0)
     *         ****** ******
     *         ****** ******
     *         ****** ******
     *     y1  ** *** *** **   (x0, y1)  (x2, y1)
     *         *** ** ** ***
     *         **** * * ****
     *         *****   *****
     *         ****** ******        (x1, y2)
     *     y2  *************
     *        x0     x1     x2
     * </pre>
     *
     */
    SpecialCharacter LF = (GraphicsContext gc, Rect r) -> {
        double x0 = r.x() + r.w() * 0.1;
        double x1 = r.x() + r.w() * 0.5;
        double x2 = r.x() + r.w() * 0.9;
        double y0 = r.y() + r.h() * 0.2;
        double y1 = r.y() + r.h() * 0.5;
        double y2 = r.y() + r.h() * 0.8;
        gc.setLineWidth(1);
        gc.strokeLine(x1, y2, x0, y1);
        gc.strokeLine(x1, y2, x1, y0);
        gc.strokeLine(x1, y2, x2, y1);
    };

    /**
     * <pre>
     *     y0  *************
     *         ***** *******        (x1, y0)
     *         **** ********
     *         *** *********
     *         ** **********
     *     y1  *           *   (x0, y1)  (x2, y1)
     *         ** **********
     *         *** *********
     *         **** ********
     *         ***** *******        (x1, y2)
     *     y2  *************
     *        x0     x1     x2
     * </pre>
     */
    SpecialCharacter CRLF = (GraphicsContext gc, Rect r) -> {
        double x0 = r.x() + r.w() * 0.1;
        double x1 = r.x() + r.w() * 0.5;
        double x2 = r.x() + r.w() * 0.9;
        double y0 = r.y() + r.h() * 0.3;
        double y1 = r.y() + r.h() * 0.5;
        double y2 = r.y() + r.h() * 0.7;
        gc.setLineWidth(1);
        gc.strokeLine(x0, y1, x1, y0);
        gc.strokeLine(x0, y1, x2, y1);
        gc.strokeLine(x0, y1, x1, y2);
    };

    /**
     * <pre>
     *     y0  *************
     *         ******* *****        (x1, y0)
     *         ******** ****
     *         ********* ***
     *         ********** **
     *     y1  *           *   (x0, y1)  (x2, y1)
     *         ********** **
     *         ********* ***
     *         ******** ****
     *         ******* *****        (x1, y2)
     *     y2  *************
     *        x0     x1     x2
     * </pre>
     */
    SpecialCharacter TAB = (GraphicsContext gc, Rect r) -> {
        double x0 = r.x() + r.w() * 0.1;
        double x1 = r.x() + r.w() * 0.5;
        double x2 = r.x() + r.w() * 0.9;
        double y0 = r.y() + r.h() * 0.3;
        double y1 = r.y() + r.h() * 0.5;
        double y2 = r.y() + r.h() * 0.7;
        gc.setLineWidth(1);
        gc.strokeLine(x2, y1, x1, y0);
        gc.strokeLine(x2, y1, x0, y1);
        gc.strokeLine(x2, y1, x1, y2);
    };

    /**
     * <pre>
     *     y0  *************
     *         *************
     *         *************
     *         *************
     *         *************
     *         *************
     *         *************
     *     y1  * ********* *   (x0, y1)  (x2, y1)
     *         * ********* *
     *         *           *   (x0, y2)  (x2, y2)
     *     y2  *************
     *        x0     x1     x2
     * </pre>
     */
    SpecialCharacter WSP = (GraphicsContext gc, Rect r) -> {
        double x0 = r.x() + r.w() * 0.1;
        double x2 = r.x() + r.w() * 0.9;
        double y1 = r.y() + r.h() * 0.7;
        double y2 = r.y() + r.h() * 0.9;
        gc.setLineWidth(1);
        gc.strokeLine(x0, y1, x0, y2);
        gc.strokeLine(x0, y2, x2, y2);
        gc.strokeLine(x2, y2, x2, y1);
    };

}
