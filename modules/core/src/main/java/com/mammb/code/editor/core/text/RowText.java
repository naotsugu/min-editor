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

/**
 * The RowText.
 * @author Naotsugu Kobayashi
 */
public interface RowText extends LinedText {

    @Override
    default int line() {
        return row();
    }

    /**
     * Create a new {@link RowText}.
     * @param row the row number
     * @param text the text
     * @param fm the font metrics
     * @return a new {@link RowText}
     */
    static RowText of(int row, String text, FontMetrics fm) {
        return of(row, text, fm, true);
    }

    /**
     * Create a new {@link RowText}.
     * @param row the row number
     * @param text the text
     * @param fm the font metrics
     * @param handleTab handle tab
     * @return a new {@link RowText}
     */
    static RowText of(int row, String text, FontMetrics fm, boolean handleTab) {
        double width = 0;
        double[] advances = new double[text.length()];
        for (int i = 0, tabShift = 0; i < text.length(); i++, tabShift++) {
            char ch1 = text.charAt(i);
            if (Character.isHighSurrogate(ch1)) {
                width += advances[i] = fm.getAdvance(ch1, text.charAt(i + 1));
                i++;
            } else if (handleTab && ch1 == '\t') {
                int ts = fm.getTabSize();
                int sp = (tabShift < ts)
                    ? ts - tabShift
                    : ts - (tabShift % ts);
                tabShift += (sp - 1);
                width += advances[i] = fm.standardCharWidth() * sp;
            } else if (Character.isISOControl(ch1)) {
                i++;
            } else {
                width += advances[i] = fm.getAdvance(ch1);
            }
        }

        record RowTextRecord(int row, String value, double[] advances, double width, double height)
            implements RowText {
        }

        return new RowTextRecord(row, text, advances, width, fm.getLineHeight());
    }

}
