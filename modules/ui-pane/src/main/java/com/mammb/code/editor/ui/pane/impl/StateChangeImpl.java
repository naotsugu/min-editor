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
package com.mammb.code.editor.ui.pane.impl;

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.MetricsRecord;
import com.mammb.code.editor.model.text.LineEnding;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.StateChange;
import com.mammb.code.editor.ui.pane.Caret;

import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * StateChangeImpl.
 * @author Naotsugu Kobayashi
 */
public class StateChangeImpl implements StateChange {

    private Metrics prevMetrics;
    private OffsetPoint prevCaretPoint;
    private Consumer<LineEnding> lineEndingHandler;
    private Consumer<Charset> charsetHandler;
    private Consumer<OffsetPoint> caretPointHandler;

    public void push(Metrics metrics, Caret caret) {
        push(metrics);
        push(caret);
    }

    public void push(Metrics metrics) {
        if (prevMetrics == null || prevMetrics.lineEnding() != metrics.lineEnding()) {
            lineEndingHandler.accept(metrics.lineEnding());
        }
        if (prevMetrics == null || !prevMetrics.charset().equals(metrics.charset())) {

            charsetHandler.accept(metrics.charset());
        }
        prevMetrics = new MetricsRecord(metrics);
    }

    public void push(Caret caret) {
        OffsetPoint caretPoint = caret.caretPoint();
        if (prevCaretPoint == null || !prevCaretPoint.equals(caretPoint)) {
            caretPointHandler.accept(caretPoint);
        }
        prevCaretPoint = caretPoint;
    }

    @Override
    public void lineEndingListener(Consumer<LineEnding> handler) {
        this.lineEndingHandler = handler;
    }

    @Override
    public void charsetListener(Consumer<Charset> handler) {
        this.charsetHandler = handler;
    }

    @Override
    public void caretPointListener(Consumer<OffsetPoint> handler) {
        this.caretPointHandler = handler;
    }

}
