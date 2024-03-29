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
package com.mammb.code.editor.model.slice.impl;

import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.slice.TextualSlice;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.until.Traverse;
import com.mammb.code.editor.model.until.Until;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * RowSlice.
 * @author Naotsugu Kobayashi
 */
public class RowSlice implements TextualSlice<Textual> {

    /** The row list. */
    private final List<Textual> rows = new LinkedList<>();

    /** The row source. */
    private final RowSupplier rowSupplier;

    /** The row size of slice. */
    private int maxRowSize = 10;


    /**
     * Constructor.
     * @param maxRowSize the row size of slice
     * @param rowSupplier the row source
     */
    public RowSlice(int maxRowSize, RowSupplier rowSupplier) {
        this.rowSupplier = Objects.requireNonNull(rowSupplier);
        this.maxRowSize = Math.max(maxRowSize, 1);
    }


    @Override
    public List<Textual> texts() {
        if (rows.isEmpty()) {
            fulfillRows();
        }
        return rows;
    }


    @Override
    public int pageSize() {
        return maxRowSize;
    }


    @Override
    public void setPageSize(int capacity) {

        if (capacity <= 1) {
            throw new IllegalArgumentException(
                "Too small capacity. [%d]".formatted(capacity));
        }

        if (maxRowSize > capacity) {
            this.maxRowSize = capacity;
            if (rows.size() > capacity) {
                rows.subList(capacity, rows.size()).clear();
            }
        } else if (maxRowSize < capacity) {
            this.maxRowSize = capacity;
            fulfillRows();
        }
    }


    @Override
    public void refresh(int rowNumber) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).offsetPoint().row() >= rowNumber) {
                rows.subList(i, rows.size()).clear();
                fulfillRows();
                return;
            }
        }
    }


    @Override
    public boolean move(OffsetPoint base, int rowDelta) {

        OffsetPoint point;
        if (rowDelta == 0) {
            point = base;
        } else {
            Traverse traverse;
            if (rowDelta > 0) {
                // forward
                traverse = Traverse.forwardOf(base);
                var until = Until.lfInclusive(rowDelta).withLess(traverse);
                rowSupplier.offset(base.cpOffset(), until);
            } else {
                // backward
                traverse = Traverse.backwardOf(base);
                var until = Until.lf(Math.abs(rowDelta)).withLess(traverse);
                rowSupplier.offsetBefore(base.cpOffset(), until);
            }
            point = traverse.asOffsetPoint();
        }
        if (!rows.isEmpty() && rows.getFirst().offsetPoint().equals(point)) {
            return false;
        }

        rows.clear();
        for (int i = 0; i < maxRowSize; i++) {
            String str = rowSupplier.at(point.cpOffset());
            rows.add(Textual.of(point, str));
            if (str.isEmpty() || str.charAt(str.length() - 1) != '\n') {
                break;
            }
            point = point.plus(str);
        }
        return true;
    }


    @Override
    public List<Textual> prev(int n) {

        List<Textual> added = new LinkedList<>();

        for (int i = 0; i < n; i++) {

            Textual head = rows.getFirst();
            long cpOffset = head.offsetPoint().cpOffset();
            if (cpOffset == 0) break;

            String str = rowSupplier.before(cpOffset);
            Textual textual = Textual.of(head.offsetPoint().minus(str), str);
            added.addFirst(textual);
            pushFirst(textual);
        }

        return added;
    }


    @Override
    public List<Textual> next(int n) {

        List<Textual> added = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            Textual tail = rows.getLast();
            if (tail.isEmpty() || tail.text().charAt(tail.text().length() - 1) != '\n') {
                break;
            }

            OffsetPoint next = tail.offsetPoint().plus(tail.text());
            String str = rowSupplier.at(next.cpOffset());
            Textual textual = Textual.of(next, str);
            added.add(textual);
            pushLast(textual);

        }
        return added;
    }


    private void pushFirst(Textual row) {
        rows.addFirst(row);
        while (rows.size() > maxRowSize) {
            rows.removeLast();
        }
    }


    private void pushLast(Textual row) {
        rows.add(row);
        while (rows.size() > maxRowSize) {
            rows.removeFirst();
        }
    }


    /**
     * Fill the row buffer.
     */
    private void fulfillRows() {

        if (rows.size() > maxRowSize) {
            rows.subList(maxRowSize, rows.size()).clear();
            return;
        }

        while (rows.size() < maxRowSize) {

            OffsetPoint next = OffsetPoint.zero;
            if (!rows.isEmpty()) {
                Textual tail = rows.getLast();
                if (isEof(tail)) return;
                next = tail.offsetPoint().plus(tail.text());
            }

            String str = rowSupplier.at(next.cpOffset());
            Textual textual = Textual.of(next, str);
            rows.add(textual);
            if (isEof(textual)) return;
        }

    }


    /**
     * Gets whether the specified line is the last line or not.
     * @param line the specified line
     * @return {@code true}, if the specified line is the last line
     */
    private boolean isEof(Textual line) {
        return line.isEmpty() ||
            line.text().charAt(line.length() - 1) != '\n';
    }

}
