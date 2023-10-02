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
import com.mammb.code.editor.ui.pane.StateChange;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * StateChangeImpl.
 * @author Naotsugu Kobayashi
 */
public class StateChangeImpl implements StateChange {

    private Metrics prev;
    private Consumer<LineEnding> lineEndingHandler;
    private Consumer<Charset> charsetHandler;

    public void push(Metrics metrics) {
        if (prev == null || prev.lineEnding() != metrics.lineEnding()) {
            lineEndingHandler.accept(metrics.lineEnding());
        }
        if (prev == null || !prev.path().equals(metrics.path())) {
            charsetHandler.accept(metrics.charset());
        }
        prev = new MetricsRecord(metrics);
    }

    @Override
    public void lineEnding(Consumer<LineEnding> handler) {
        this.lineEndingHandler = handler;
    }

    @Override
    public void charset(Consumer<Charset> handler) {
        this.charsetHandler = handler;
    }

}
