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

public class Caret {

    /** The caret offset */
    private int charOffset = 0;
    /** The caret position x. */
    private double x = 0;
    /** The caret position y. */
    private double y = 0;
    /** The text line. */
    private LayoutLine line;
    /** The caret height. */
    private double height = 0;
    /** The logical caret position x. */
    private double logicalX = 0;


    public void moveTo(double x, double y, double height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public void moveTo(int charOffset) {
        this.charOffset = charOffset;
    }


    public Caret copy() {
        Caret copy = new Caret();
        copy.charOffset = charOffset;
        copy.x = x;
        copy.y = y;
        copy.height = height;
        copy.line = line;
        copy.logicalX = logicalX;
        return copy;
    }

    public void plusOffset() {
        charOffset++;
    }

    public void minusOffset() {
        charOffset--;
    }

    public void syncLogical() {
        logicalX = x;
    }

    public boolean equalPoint(Caret other) {
        return this.x == other.x && this.y == other.y;
    }

    public int charOffset() {
        return charOffset;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double y2() {
        return y + height();
    }

    public double height() {
        return height;
    }

    public double logicalX() {
        return logicalX;
    }

}
