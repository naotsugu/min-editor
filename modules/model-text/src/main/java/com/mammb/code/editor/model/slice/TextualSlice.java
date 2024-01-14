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
package com.mammb.code.editor.model.slice;

import com.mammb.code.editor.model.slice.impl.RowSlice;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.text.TextualScroll;

import java.util.List;

/**
 * RowSlice.
 * @param <T> type of row content
 * @author Naotsugu Kobayashi
 */
public interface TextualSlice<T extends Textual> extends TextualScroll<T> {

    @Override
    List<T> texts();

    @Override
    int pageSize();

    @Override
    List<T> prev(int n);

    @Override
    List<T> next(int n);

    @Override
    void setPageSize(int capacity);

    @Override
    boolean move(OffsetPoint base, int rowDelta);

    /**
     * Refresh below the specified rowNumber(inclusive).
     * @param rowNumber the specified rowNumber(inclusive)
     */
    void refresh(int rowNumber);


    /**
     * Create a new RowSlice from the specified size and rowSupplier.
     * @param maxRowSize the row size of slice
     * @param rowSupplier the row supplier
     * @return the created RowSlice
     */
    static TextualSlice<Textual> of(int maxRowSize, RowSupplier rowSupplier) {
        return new RowSlice(maxRowSize, rowSupplier);
    }

}
