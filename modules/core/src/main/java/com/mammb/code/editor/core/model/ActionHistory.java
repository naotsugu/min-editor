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
            return actions.add(action);

        } else {
            actions.clear();
            return false;
        }
    }

    /**
     * Get the action event repetition.
     * @return the action event repetition
     */
    public List<Action> repetition() {

        if (actions.size() == 1) {
            return List.of(actions.getFirst());
        }

        int mid = actions.size() / 2;
        for (int i = mid; mid > 1; mid--) {
            int index = actions.size() - i;
            List<Action> l = actions.subList(index - i, i);
            List<Action> r = actions.subList(i, actions.size());
            if (equals(l, r)) {
                List<Action> list = new ArrayList<>(r);
                actions.clear();
                actions.addAll(list);
                return list;
            }
        }

        return Collections.emptyList();
    }

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
