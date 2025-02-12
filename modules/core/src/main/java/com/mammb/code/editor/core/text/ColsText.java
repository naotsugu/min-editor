/*
 * Copyright 2023-2025 the original author or authors.
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

import com.mammb.code.editor.core.FontMetrics;
import java.util.Arrays;

/**
 * The ColsText.
 * @author Naotsugu Kobayashi
 */
public class ColsText implements RowText {

    /** The number of row. */
    private final int row;
    /** The text value. */
    private final String value;
    /** The advances. */
    private final double[] advances;
    /** The width. */
    private final double width;
    /** The height. */
    private final double height;

    private double[] partitionWidths;
    private final int[] colsLength;


    public ColsText(int row, String text, FontMetrics fm, String separater) {

        this.row = row;
        this.value = text;
        this.height = fm.getLineHeight();
        this.advances = new double[text.length()];
        this.colsLength = Arrays.stream(text.split(separater))
            .mapToInt(String::length).toArray();

        double w = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch1 = text.charAt(i);
            if (Character.isHighSurrogate(ch1)) {
                w += advances[i] = fm.getAdvance(ch1, text.charAt(i + 1));
                i++;
            } else if (Character.isISOControl(ch1)) {
                i++;
            } else {
                w += advances[i] = fm.getAdvance(ch1);
            }
        }
        this.width = w;
    }

    @Override
    public int row() {
        return row;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public double[] advances() {
        return advances;
    }

    @Override
    public double width() {
        return width;
    }

    @Override
    public double height() {
        return height;
    }

}
