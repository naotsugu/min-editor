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
package com.mammb.code.editor.core.layout;

import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The SubRange.
 * @author Naotsugu Kobayashi
 */
public class SubRange implements Comparable<SubRange> {

    /** The number of row. */
    private int row;
    /** The line number of the split row. */
    private final int subLine;
    /** The number of split lines. */
    private final int subLines;
    /** The starting index on the row of this line. */
    private final int fromIndex;
    /** The ending index on the row of this line. */
    private final int toIndex;

    /**
     * Constructor.
     * @param row the number of row
     * @param subLine the line number of the split row
     * @param subLines the number of split lines
     * @param fromIndex the starting index on the row of this line
     * @param toIndex the ending index on the row of this line
     */
    SubRange(int row, int subLine, int subLines, int fromIndex, int toIndex) {
        this.row = row;
        this.subLine = subLine;
        this.subLines = subLines;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public void plusRow(int n) {
        row += n;
    }

    /**
     * Get the number of row.
     * @return the number of row
     */
    public int row() {
        return row;
    }

    /**
     * Get the line number of the split row.
     * @return the line number of the split row
     */
    public int subLine() {
        return subLine;
    }

    /**
     * Get the number of split lines.
     * @return the number of split lines
     */
    public int subLines() {
        return subLines;
    }

    /**
     * Get the starting index on the row of this line.
     * @return the starting index on the row of this line
     */
    public int fromIndex() {
        return fromIndex;
    }

    /**
     * The ending index on the row of this line.
     * @return the ending index on the row of this line
     */
    public int toIndex() {
        return toIndex;
    }

    /**
     * Get the length of this range.
     * @return the length of this range
     */
    public int length() {
        return toIndex - fromIndex;
    }

    /**
     * Get whether the specified rows and columns are included in this {@link SubRange}.
     * @param row the row number
     * @param col the column number
     * @return {@code true}, if the specified rows and columns are included in this {@link SubRange}
     */
    boolean contains(int row, int col) {
        return this.row == row && (
            (this.fromIndex <= col && col < this.toIndex) ||
                (col == 0 && fromIndex == 0 && toIndex == 0) ||
                (col == this.toIndex && subLine == subLines - 1)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubRange subRange = (SubRange) o;
        return row == subRange.row &&
            subLine == subRange.subLine &&
            subLines == subRange.subLines &&
            fromIndex == subRange.fromIndex &&
            toIndex == subRange.toIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, subLine, subLines, fromIndex, toIndex);
    }

    @Override
    public int compareTo(SubRange that) {
        return Comparator.comparing(SubRange::row)
            .thenComparing(SubRange::subLine)
            .compare(this, that);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SubRange.class.getSimpleName() + "[", "]")
            .add("row=" + row)
            .add("subLine=" + subLine)
            .add("subLines=" + subLines)
            .add("fromIndex=" + fromIndex)
            .add("toIndex=" + toIndex)
            .toString();
    }

}
