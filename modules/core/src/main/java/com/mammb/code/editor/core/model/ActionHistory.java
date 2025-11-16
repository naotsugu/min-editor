/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.mammb.code.editor.core.Action;

/**
 * The action history.
 * @author Naotsugu Kobayashi
 */
public class ActionHistory {

    /** The items. */
    private final List<Action> actions = new ArrayList<>();

    /**
     * Offer the action event.
     * @param action the action event
     * @return {@code true} if the event offered
     */
    public boolean offer(Action action) {

        Objects.requireNonNull(action);

        if (action instanceof ActionRecords.Empty || action instanceof ActionRecords.Repeat) {
            return false;
        }

        if (action instanceof Action.Repeatable) {

            if (!actions.isEmpty() &&
                actions.getLast().occurredAt() + 3_000 < action.occurredAt()) {
                actions.clear();
            }
            if (actions.size() > 100) {
                // delete too much history.
                actions.subList(0, actions.size() - 100).clear();
            }
            return actions.add(action);

        } else {
            actions.clear();
            return false;
        }
    }

    /**
     * Peek the last action.
     * @return the last action
     */
    public Action peekLast() {
        return actions.isEmpty() ? Action.empty() : actions.getLast();
    }

    /**
     * Get the action event repetition.
     * @return the action event repetition
     */
    public List<Action> repetition() {

        if (actions.size() == 1) {
            return List.of(actions.getFirst());
        }

        int mid = (int) Math.ceil(actions.size() / 2.0);
        for (int i = mid; i < actions.size(); i++) {
            List<Action> r = actions.subList(i, actions.size());
            List<Action> l = actions.subList(i - r.size(), i);
            if (equals(l, r)) {
                List<Action> list = new ArrayList<>(r);
                actions.clear();
                actions.addAll(list);
                return list;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get whether the specified action list is equal or not.
     * @param l the left hands
     * @param r the right hands
     * @return {@code true}, if the specified action list is equal
     */
    private boolean equals(List<Action> l, List<Action> r) {
        if (l.size() != r.size()) {
            return false;
        }
        for (int i = 0; i < l.size(); i++) {
            var la = l.get(i);
            var ra = r.get(i);
            if (!Objects.equals(la.getClass(), ra.getClass())) {
                return false;
            }
            if (la instanceof Action.WithAttr<?>) {
                var lo = ((Action.WithAttr<?>) la).attr();
                var ro = ((Action.WithAttr<?>) ra).attr();
                if (!Objects.equals(lo, ro)) {
                    return false;
                }
            }
        }
        return true;
    }

}
