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
package com.mammb.code.editor.model.find.impl;

import com.mammb.code.editor.model.find.Find;
import com.mammb.code.editor.model.find.FindSpec;
import com.mammb.code.editor.model.find.Match;
import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Find.
 * @author Naotsugu Kobayashi
 */
public class FindImpl implements Find {

    /** The row supplier. */
    private RowSupplier rowSupplier;

    /** The listeners of find. */
    private List<Consumer<Textual>> listeners;

    private volatile boolean interrupt = false;


    public FindImpl(RowSupplier rowSupplier, List<Consumer<Textual>> listeners) {
        this.rowSupplier = rowSupplier;
        this.listeners = listeners;
    }

    public FindImpl(RowSupplier rowSupplier) {
        this(rowSupplier, new ArrayList<>());
    }

    @Override
    public void run(OffsetPoint base, FindSpec spec) {
        OffsetPoint point = base;
        for (;;) {
            if (interrupt) {
                interrupt = false;
                return;
            }
            String row = rowSupplier.at(point.cpOffset());
            if (row == null) {
                break;
            }

            var result = spec.match(row);
            for (Match match : result) {
                Textual textual = Textual.of(
                    point.plus(row.substring(0, match.start())),
                    row.substring(match.start(), match.end()));
                listeners.forEach(listener -> listener.accept(textual));
                if (spec.oneshot()) {
                    return;
                }
            }
            point = point.plus(row);
        }
    }

    @Override
    public void cancel() {
        this.interrupt = true;
    }

    @Override
    public void addListener(Consumer<Textual> listener) {
        listeners.add(listener);
    }

}
