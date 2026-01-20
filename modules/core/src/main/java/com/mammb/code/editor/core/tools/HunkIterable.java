/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.core.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * The hunk iterable.
 * @author Naotsugu Kobayashi
 */
public class HunkIterable<T extends CharSequence> implements Iterable<T> {

    private final List<Integer> rows;
    private final Function<Integer, CharSequence> rowToText;
    private final int contextSize;

    public HunkIterable(Collection<Integer> rows, int contextSize, Function<Integer, CharSequence> rowToText) {
        this.rows = rows.stream().sorted().toList();
        this.contextSize = contextSize;
        this.rowToText = rowToText;
    }

    @Override
    public Iterator<T> iterator() {
        return rows.isEmpty()
            ? Collections.emptyIterator()
            : build();
    }

    private Iterator<T> build() {

        return new Iterator<T>() {

            private int rowIndex = Math.max(0, rows.getFirst() - contextSize);

            @Override
            public boolean hasNext() {
                // TODO
                return false;
            }

            @Override
            public T next() {
                // TODO
                return null;
            }
        };

    }

    private String formatString() {
        if (rows.isEmpty()) return "%d";
        return "%" + String.valueOf(rows.getLast()).length() + "d";
    }

}
