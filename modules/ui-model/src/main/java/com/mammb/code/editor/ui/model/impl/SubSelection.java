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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SubSelection.
 * @author Naotsugu Kobayashi
 */
public class SubSelection {

    record Pair(OffsetPoint point, CaretLine caretLine) { }

    private final List<Pair> pairs;


    public SubSelection(List<CaretLine> moonRefs) {
        this.pairs = moonRefs.stream()
            .map(SubSelection::pairOf)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }


    public void clear() {
        pairs.clear();
    }


    private void ensureScope() {
        pairs.removeIf(p -> p.caretLine().getLine() == null);
    }


    private static Pair pairOf(CaretLine caretLine) {
        if (caretLine == null) {
            return null;
        }
        OffsetPoint p = caretLine.point();
        return (p == null) ? null : new Pair(p, caretLine);
    }

}
