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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The ColsText.
 * @author Naotsugu Kobayashi
 */
public class ColsText implements RowText {

    /** The peer row text. */
    private final RowText peer;
    /** The advances. */
    private final double[] advances;
    /** The raw widths. */
    private final double[] rawWidths;
    /** The column lengths. */
    private final int[] colLengths;
    /** The margin. */
    private final double margin;

    /**
     * Constructor.
     * @param row the number of rows
     * @param text the text value
     * @param fm the font metrics
     * @param separator the separator
     */
    public ColsText(int row, String text, FontMetrics fm, String separator) {
        peer = RowText.of(row, text, fm, false);
        advances = Arrays.copyOf(peer.advances(), peer.advances().length);
        margin = fm.standardCharWidth() * 2;
        colLengths = Arrays.stream(split(text, separator))
            .mapToInt(String::length).toArray();
        double[] advances = peer.advances();
        rawWidths = new double[colLengths.length];
        int offset = 0;
        for (int i = 0; i < colLengths.length; i++) {
            int offsetTo = offset + colLengths[i];
            rawWidths[i] = Arrays.stream(advances, offset, offsetTo).sum();
            offset = offsetTo + 1;
        }
    }

    /**
     * Fix column width.
     * @param colsWidth the column width list
     */
    public void fixCols(List<Double> colsWidth) {
        if (advances.length == 0) return;
        int offset = 0;
        for (int i = 0; i < colsWidth.size() && i < colLengths.length; i++) {
            offset += colLengths[i];
            if (offset >= advances.length) break;
            double width = colsWidth.get(i) - rawWidths[i];
            advances[offset++] = Math.max(width, 0) + margin;
        }
    }

    @Override
    public int row() {
        return peer.row();
    }

    @Override
    public String value() {
        return peer.value();
    }

    @Override
    public double[] advances() {
        return advances;
    }

    @Override
    public double width() {
        return Arrays.stream(advances).sum();
    }

    @Override
    public double height() {
        return peer.height();
    }

    /**
     * Get the raw widths.
     * @return the raw widths
     */
    public double[] rawWidths() {
        return rawWidths;
    }

    /**
     * Splits the provided text into an array of substrings using the specified separator.
     * The method respects quoted parts of the text and does not split within quotes.
     * Double quotes inside quoted parts are escaped using an additional double quote.
     * @param text the string to be split may be null
     * @param separator the string used as the delimiter may be null or empty
     * @return an array of split substrings; returns an empty array if the input text is null
     *         or an array containing the original text if the separator is null or empty
     */
    static String[] split(String text, String separator) {

        if (text == null) return new String[0];
        if (text.isEmpty() || separator == null || separator.isEmpty()) return new String[] { text };

        var result = new ArrayList<String>();
        boolean inQuotes = false;
        int beginIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (!inQuotes && text.startsWith(separator, i)) {
                result.add(text.substring(beginIndex, i));
                i += separator.length() - 1;
                beginIndex = i + 1;
                continue;
            }

            if (c == '"') {
                if (inQuotes) {
                    if (i < text.length() - 1 && text.charAt(i + 1) == '"') {
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    inQuotes = true;
                }
            }
        }
        result.add(text.substring(beginIndex));
        return result.toArray(new String[0]);
    }

}
