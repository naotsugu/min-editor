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
package com.mammb.code.editor.core.text;

import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.Rgba;

/**
 * The symbols.
 * @author Naotsugu Kobayashi
 */
public interface Symbols {

    /**
     * The line feed symbol.
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
     */
    static Draw.Line[] lineFeed(double x, double y, double w, double h, Rgba color) {
        double x0 = x + w * 0.1;
        double x1 = x + w * 0.5;
        double x2 = x + w * 0.9;
        double y0 = y + h * 0.2;
        double y1 = y + h * 0.5;
        double y2 = y + h * 0.8;

        Draw.Line[] lines = new Draw.Line[3];
        lines[0] = new Draw.Line(x1, y0, x1, y2, color);
        lines[1] = new Draw.Line(x0, y1, x1, y2, color);
        lines[2] = new Draw.Line(x2, y1, x1, y2, color);
        return lines;
    }


    /**
     * The carriage return - line feed symbol.
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
    static Draw.Line[] crlf(double x, double y, double w, double h, Rgba color) {
        double x0 = x + w * 0.1;
        double x1 = x + w * 0.5;
        double x2 = x + w * 0.9;
        double y0 = y + h * 0.3;
        double y1 = y + h * 0.5;
        double y2 = y + h * 0.7;

        Draw.Line[] lines = new Draw.Line[3];
        lines[0] = new Draw.Line(x0, y1, x1, y0, color);
        lines[1] = new Draw.Line(x0, y1, x2, y1, color);
        lines[2] = new Draw.Line(x0, y1, x1, y2, color);
        return lines;
    }

    /**
     * The tab symbol.
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
    static Draw.Line[] tab(double x, double y, double w, double h, Rgba color) {
        double x0 = x + w * 0.1;
        double x1 = x + w * 0.5;
        double x2 = x + w * 0.9;
        double y0 = y + h * 0.3;
        double y1 = y + h * 0.5;
        double y2 = y + h * 0.7;

        Draw.Line[] lines = new Draw.Line[3];
        lines[0] = new Draw.Line(x2, y1, x1, y0, color);
        lines[1] = new Draw.Line(x2, y1, x0, y1, color);
        lines[2] = new Draw.Line(x2, y1, x1, y2, color);
        return lines;
    }

    /**
     * The white space symbol.
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
    static Draw.Line[] whiteSpace(double x, double y, double w, double h, Rgba color) {
        double x0 = x + w * 0.1;
        double x2 = x + w * 0.9;
        double y1 = y + h * 0.7;
        double y2 = y + h * 0.9;
        Draw.Line[] lines = new Draw.Line[3];
        lines[0] = new Draw.Line(x0, y1, x0, y2, color);
        lines[1] = new Draw.Line(x0, y2, x2, y2, color);
        lines[2] = new Draw.Line(x2, y2, x2, y1, color);
        return lines;
    }

}
