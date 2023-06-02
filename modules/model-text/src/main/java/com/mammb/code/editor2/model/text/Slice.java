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
package com.mammb.code.editor2.model.text;

import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.core.TextList;
import com.mammb.code.editor2.model.core.Textual;
import com.mammb.code.editor2.model.text.impl.RowSlice;

import java.util.List;

/**
 * RowSlice.
 * @param <T> type of row content
 * @author Naotsugu Kobayashi
 */
public interface Slice<T extends Textual> extends TextList<T> {

    @Override
    List<T> texts();

    /**
     * Get the capacity of row.
     * @return the capacity of row
     */
    int capacity();

    /**
     * Create a new RowSlice from the specified size and rowSupplier.
     * @param maxRowSize the row size of slice
     * @param rowSupplier the row supplier
     * @return the created RowSlice
     */
    static Slice<PointText> of(int maxRowSize, RowSupplier rowSupplier) {
        return new RowSlice(maxRowSize, rowSupplier);
    }

}
