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
package com.mammb.code.editor.ui.pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The action event history.
 * @author Naotsugu Kobayashi
 */
public class ActionHistory {

    /** The items. */
    private final List<Action> items;


    /**
     * Constructor.
     */
    public ActionHistory() {
        this.items = new ArrayList<>();
    }


    /**
     * Get the action event repetition.
     * @return the action event repetition
     */
    public List<Action> repetition() {
        if (items.size() < 2) {
            return Collections.emptyList();
        }

        int mid = items.size() / 2;
        for (int i = mid; mid > 1; mid--) {
            int index = items.size() - i;
            List<Action> l = items.subList(index - i, i);
            List<Action> r = items.subList(i, items.size());
            if (equals(l, r)) {
                return r;
            }
        }

        return Collections.emptyList();
    }


    /**
     * Get the current history size.
     * @return the current history size
     */
    public int size() {
        return items.size();
    }


    /**
     * Offer the action event.
     * @param event the action event
     * @return {@code true} if the event offered
     */
    public boolean offer(Action event) {

        if (event == null || event.type() == Action.Type.REPEAT || event.type() == Action.Type.EMPTY) {
            return false;
        }

        if (!event.isRepeatable()) {
            items.clear();
            return false;
        }

        if (items.size() > 0 &&
            items.getLast().occurredAt() + 3_000 < event.occurredAt()) {
            items.clear();
        }
        return items.add(event);

    }


    /**
     * Pool the action event.
     * @return the action event
     */
    public Action poll() {
        return items.isEmpty() ? null : items.removeFirst();
    }


    /**
     * Peek the action event.
     * @return the action event
     */
    public Action peek() {
        return items.isEmpty() ? null : items.getFirst();
    }


    private boolean equals(List<Action> l, List<Action> r) {
        if (l.size() != r.size()) {
            return false;
        }
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).type() != r.get(i).type()) {
                return false;
            }
        }
        return true;
    }

}
