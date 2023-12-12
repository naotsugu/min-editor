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

    private List<Consumer<LineEndingSymbol>> lineEndingHandlers = new ArrayList<>();
    private List<Consumer<Charset>> charsetHandlers = new ArrayList<>();
    private List<Consumer<CaretPoint>> caretPointHandlers = new ArrayList<>();
    private List<Consumer<Range>> selectionHandlers = new ArrayList<>();

    @Override
    public void push(Metrics metrics, Caret caret, Selection selection) {
        push(metrics);
        push(caret);
        push(selection);
    }

    @Override
    public void addLineEndingChanged(Consumer<LineEndingSymbol> handler) {
        Objects.requireNonNull(handler);
        this.lineEndingHandlers.add(handler);
    }

    @Override
    public void addCharsetChanged(Consumer<Charset> handler) {
        Objects.requireNonNull(handler);
        this.charsetHandlers.add(handler);
    }

    @Override
    public void addCaretPointChanged(Consumer<CaretPoint> handler) {
        Objects.requireNonNull(handler);
        this.caretPointHandlers.add(handler);
    }

    @Override
    public void addSelectionChanged(Consumer<Range> handler) {
        Objects.requireNonNull(handler);
        this.selectionHandlers.add(handler);
    }


    private void push(Metrics metrics) {
        if (prevMetrics == null || prevMetrics.lineEnding() != metrics.lineEnding()) {
            lineEndingHandlers.forEach(h -> h.accept(new LineEndingSymbol(metrics.lineEnding())));
        }
        if (prevMetrics == null || !prevMetrics.charset().equals(metrics.charset())) {
            charsetHandlers.forEach(h -> h.accept(metrics.charset()));
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
