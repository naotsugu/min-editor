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
package com.mammb.code.editor.model.find.impl;

import com.mammb.code.editor.model.find.Find;
import com.mammb.code.editor.model.find.FindSpec;
import com.mammb.code.editor.model.find.Found;
import com.mammb.code.editor.model.find.FoundNone;
import com.mammb.code.editor.model.find.FoundReset;
import com.mammb.code.editor.model.find.FoundRun;
import com.mammb.code.editor.model.find.Match;
import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.text.OffsetPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Find.
 * @author Naotsugu Kobayashi
 */
public class FindImpl implements Find {

    /** The row supplier. */
    private final RowSupplier rowSupplier;

    /** The listeners of find. */
    private final List<Consumer<Found>> listeners;

    /** The interrupt marker. */
    private volatile boolean interrupt = false;


    /**
     * Constructor.
     * @param rowSupplier the row supplier
     * @param listeners the listeners of find
     */
    private FindImpl(RowSupplier rowSupplier, List<Consumer<Found>> listeners) {
        this.rowSupplier = rowSupplier;
        this.listeners = listeners;
    }


    /**
     * Create a new Find.
     * @param rowSupplier the row supplier
     */
    public FindImpl(RowSupplier rowSupplier) {
        this(rowSupplier, new ArrayList<>());
    }


    @Override
    public void run(OffsetPoint base, FindSpec spec) {
        int count = spec.forward()
            ? findForward(base, spec)
            : findBackward(base, spec);
        if (count == 0) listeners.forEach(listener -> listener.accept(new FoundNone()));
    }


    private int findForward(OffsetPoint base, FindSpec spec) {
        int count = 0;
        boolean searchedToTail = false;
        OffsetPoint point = base;
        for (;;) {
            if (interrupt) {
                interrupt = false;
                return count;
            }

            String text = rowSupplier.at(point.cpOffset());
            if (text.isEmpty()) {
                searchedToTail = true;
                if (base.cpOffset() != 0) {
                    point = OffsetPoint.zero; // continues from the head
                }
            }

            if (searchedToTail && point.cpOffset() >= base.cpOffset()) {
                break;
            }

            for (Match match : spec.match(text)) {
                fire(text, point, true, match);
                count++;
                if (spec.once()) {
                    return count;
                }
            }
            point = point.plus(text);
        }
        return count;
    }


    private int findBackward(OffsetPoint base, FindSpec spec) {
        int count = 0;
        OffsetPoint point = base;
        for (;;) {
            if (interrupt) {
                interrupt = false;
                return count;
            }
            if (point.cpOffset() <= 0) {
                break;
            }
            String text = rowSupplier.before(point.cpOffset());
            if (text.isEmpty()) {
                break;
            }
            point = point.minus(text);
            var results = spec.match(text);
            for (int i = results.length - 1; i >= 0; i--) {
                fire(text, point, false, results[i]);
                count++;
                if (spec.once()) {
                    return count;
                }
            }
        }
        return count;
    }


    private void fire(String text, OffsetPoint point, boolean right, Match match) {
        int margin = 60;
        int peripheralStart = Math.max(0, match.start() - margin);
        int peripheralEnd = Math.clamp(match.end() + margin, match.end(), text.length());
        var found = new FoundRun(
            point.offset() + match.start(),
            match.length(),
            right,
            text.substring(peripheralStart, peripheralEnd),
            Math.toIntExact(point.offset() - peripheralStart),
            point.row());
        listeners.forEach(listener -> listener.accept(found));
    }


    @Override
    public void cancel() {
        this.interrupt = true;
    }


    @Override
    public void reset() {
        listeners.forEach(listener -> listener.accept(new FoundReset()));
    }


    @Override
    public void addListener(Consumer<Found> listener) {
        listeners.add(listener);
    }


    @Override
    public void removeListener(Consumer<Found> listener) {
        listeners.remove(listener);
    }

}
