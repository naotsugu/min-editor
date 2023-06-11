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
package com.mammb.code.editor2.model.slice.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import com.mammb.code.editor2.model.text.PointText;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.slice.RowSupplier;
import com.mammb.code.editor2.model.slice.Slice;

/**
 * RowSlice.
 * @author Naotsugu Kobayashi
 */
public class RowSlice implements Slice<PointText> {

    /** The row list. */
    private final List<PointText> texts = new LinkedList<>();

    /** The row source. */
    private RowSupplier rowSupplier;

    /** The row size of slice. */
    private int maxRowSize = 10;


    /**
     * Constructor.
     * @param maxRowSize the row size of slice
     * @param rowSupplier the row source
     */
    public RowSlice(int maxRowSize, RowSupplier rowSupplier) {
        pushEmptyIf();
        this.rowSupplier = Objects.requireNonNull(rowSupplier);
        this.maxRowSize = Math.max(maxRowSize, 1);
    }


    @Override
    public List<PointText> texts() {
        return texts;
    }


    @Override
    public int capacity() {
        return maxRowSize;
    }


    public void refresh(int rowNumber) {
        for (int i = 0; i < texts.size(); i++) {
            if (texts.get(i).point().row() >= rowNumber) {
                texts.subList(i, texts.size()).clear();
                break;
            }
        }
        fill();
    }


    public void clear() {
        texts.clear();
        pushEmptyIf();
    }


    private void fill() {
        pushEmptyIf();
        while (texts.size() <= maxRowSize) {
            PointText tail = texts.get(texts.size() - 1);
            OffsetPoint next = tail.point().plus(tail.text());

            String str = rowSupplier.at(next.cpOffset());
            if (str == null) break;
            texts.add(PointText.of(next, str));
        }
    }


    public void prev(int n) {
        for (int i = 0; i < n; i++) {
            PointText head = texts.get(0);
            int cpOffset = head.point().cpOffset();
            if (cpOffset == 0) break;

            String str = rowSupplier.before(cpOffset);
            pushFirst(PointText.of(head.point().minus(str), str));
        }
    }


    public void next(int n) {
        for (int i = 0; i < n; i++) {
            PointText tail = texts.get(texts.size() - 1);
            OffsetPoint next = tail.point().plus(tail.text());

            String str = rowSupplier.at(next.cpOffset());
            if (str == null) break;
            pushLast(PointText.of(next, str));
        }
    }


    private void pushFirst(PointText... rows) {
        texts.addAll(0, Arrays.asList(rows));
        while (texts.size() > maxRowSize) {
            texts.remove(texts.size() - 1);
        }
    }


    private void pushLast(PointText... rows) {
        texts.addAll(Arrays.asList(rows));
        while (texts.size() > maxRowSize) {
            texts.remove(0);
        }
    }


    private void pushEmptyIf() {
        if (texts.isEmpty()) texts.add(PointText.of(OffsetPoint.zero, ""));
    }

}
