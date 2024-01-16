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

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.MetricsRecord;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.Selection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * StateChangeImpl.
 * @author Naotsugu Kobayashi
 */
public class StateChangeImpl implements StateChange {

    private Metrics prevMetrics;
    private OffsetPoint prevCaretPoint;
    private Range prevSelectionRange;

    private final List<Consumer<LineEndingSymbol>> lineEndingHandlers = new ArrayList<>();
    private final List<Consumer<Charset>> charsetHandlers = new ArrayList<>();
    private final List<Consumer<CaretPoint>> caretPointHandlers = new ArrayList<>();
    private final List<Consumer<Range>> selectionHandlers = new ArrayList<>();
    private final List<Consumer<Boolean>> contentModifyHandlers = new ArrayList<>();

    @Override
    public void push(Metrics metrics, Caret caret, Selection selection) {
        push(metrics);
        push(caret);
        push(selection);
    }

    @Override
    public void addLineEndingChanged(Consumer<LineEndingSymbol> handler) {
        lineEndingHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addCharsetChanged(Consumer<Charset> handler) {
        charsetHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addCaretPointChanged(Consumer<CaretPoint> handler) {
        caretPointHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addSelectionChanged(Consumer<Range> handler) {
        selectionHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addContentModifyChanged(Consumer<Boolean> handler) {
        contentModifyHandlers.add(Objects.requireNonNull(handler));
    }


    private void push(Metrics metrics) {
        if (prevMetrics == null || prevMetrics.lineEnding() != metrics.lineEnding()) {
            lineEndingHandlers.forEach(h -> h.accept(new LineEndingSymbol(metrics.lineEnding())));
        }
        if (prevMetrics == null || !prevMetrics.charset().equals(metrics.charset())) {
            charsetHandlers.forEach(h -> h.accept(metrics.charset()));
        }
        if (prevMetrics == null || prevMetrics.modified() != metrics.modified()) {
            contentModifyHandlers.forEach(h -> h.accept(metrics.modified()));
        }
        prevMetrics = new MetricsRecord(metrics);
    }


    private void push(Caret caret) {
        OffsetPoint caretPoint = caret.caretPoint();
        if (caretPoint == null || caretPoint.equals(prevCaretPoint)) {
            return;
        }
        caretPointHandlers.forEach(h -> h.accept(new CaretPoint(
            caretPoint.row() + 1,
            Math.toIntExact(caretPoint.cpOffset() - caret.layoutLine().point().cpOffset()),
            caretPoint.cpOffset())));
        prevCaretPoint = caretPoint;
    }

    private void push(Selection selection) {
        if (selection.length() == 0) {
            prevSelectionRange = null;
            selectionHandlers.forEach(h -> h.accept(new Range(OffsetPoint.zero, OffsetPoint.zero)));
        } else {
            var range = new Range(selection.min(), selection.max());
            if (!range.equals(prevSelectionRange)) {
                selectionHandlers.forEach(h -> h.accept(range));
                prevSelectionRange = range;
            }
        }
    }

}
