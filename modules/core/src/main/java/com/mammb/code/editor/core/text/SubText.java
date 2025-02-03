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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The sub text.
 * @author Naotsugu Kobayashi
 */
public interface SubText extends Text {

    /**
     * Get the index of from.
     * @return the index of from
     */
    int fromIndex();

    /**
     * Get the to index.
     * @return the to index.
     */
    int toIndex();

    /**
     * Get the parent text.
     * @return the parent text
     */
    Text parent();

    /**
     * Get the text of a given line of text wrapped with the specified width.
     * @param rowText the original text
     * @param width the specified width
     * @return the line wrapped text
     */
    static List<SubText> of(RowText rowText, double width) {

        if (width <= 0) {
            return List.of(new SubTextRecord(rowText, 0, rowText.length(), rowText.width()));
        }

        double w = 0;
        int fromIndex = 0;
        List<SubText> subs = new ArrayList<>();
        double[] advances = rowText.advances();
        for (int i = 0; i < rowText.length(); i++) {
            double advance = advances[i];
            if (advance <= 0) continue;
            if (w + advance > width) {
                var sub = new SubTextRecord(rowText, fromIndex, i, w);
                subs.add(sub);
                w = 0;
                fromIndex = i;
            }
            w += advance;
        }
        var sub = new SubTextRecord(rowText, fromIndex, rowText.length(), w);
        subs.add(sub);
        return subs;
    }

    /**
     * implementation of {@link SubText}.
     */
    record SubTextRecord(RowText parent, int fromIndex, int toIndex, double width) implements SubText {
        @Override
        public int row() {
            return parent.row();
        }
        @Override
        public String value() {
            return parent.value().substring(fromIndex, toIndex);
        }
        @Override
        public double[] advances() {
            return Arrays.copyOfRange(parent.advances(), fromIndex, toIndex);
        }
        @Override
        public double height() {
            return parent.height();
        }
    }

}
