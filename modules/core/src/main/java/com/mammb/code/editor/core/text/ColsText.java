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
import java.util.List;

/**
 * The ColsText.
 * @author Naotsugu Kobayashi
 */
public class ColsText implements RowText {

    /** The peer row text. */
    private final RowText peer;
    /** The raw widths. */
    private final double[] rawWidths;
    /** The column lengths. */
    private final int[] colLengths;
    /** The margin. */
    private final double margin;

    /**
     * Constructor.
     * @param row the number of row
     * @param text the text value
     * @param fm the font metrics
     * @param separater the separater
     */
    private ColsText(int row, String text, FontMetrics fm, String separater) {

        peer = RowText.of(row, text, fm, false);
        margin = fm.standardCharWidth() * 2;
        colLengths = Arrays.stream(text.split(separater))
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
     * Create the csv row text.
     * @param row the number of row
     * @param text the text value
     * @param fm the font metrics
     * @return the csv row text
     */
    public static ColsText csvOf(int row, String text, FontMetrics fm) {
        return new ColsText(row, text, fm, ",");
    }

    /**
     * Create the tsv row text.
     * @param row the number of row
     * @param text the text value
     * @param fm the font metrics
     * @return the tsv row text
     */
    public static ColsText tsvOf(int row, String text, FontMetrics fm) {
        return new ColsText(row, text, fm, "\t");
    }

    /**
     * Fix column width.
     * @param colsWidth the column width list
     */
    public void fixCols(List<Double> colsWidth) {
        double[] advances = peer.advances();
        if (advances.length == 0) return;
        int offset = 0;
        for (int i = 0; i < colsWidth.size() || i < colLengths.length; i++) {
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
        return peer.advances();
    }

    @Override
    public double width() {
        return Arrays.stream(peer.advances()).sum();
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

}
