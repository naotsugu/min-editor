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
 * ActionHistory.
 * @author Naotsugu Kobayashi
 */
public class ActionHistoryQueue {

    /** The items. */
    private final List<ActionHistory> items;

    /**
     * Constructor.
     */
    public ActionHistoryQueue() {
        this.items = new ArrayList<>();
    }

    public List<ActionHistory> repetition() {
        if (items.size() < 2) {
            return Collections.emptyList();
        }
        // | 0 | 1 |          1
        // | 0 | 1 | 2 |      1
        // | 0 | 1 | 2 | 3 |  2
        int mid = items.size() / 2;
        for (int i = mid; mid > 1; mid--) {
            int index = items.size() - i;
            List<ActionHistory> l = items.subList(index - i, i);
            List<ActionHistory> r = items.subList(i, items.size());
            if (equals(l, r)) {
                return r;
            }
        }
        return Collections.emptyList();
    }


    private boolean equals(List<ActionHistory> l, List<ActionHistory> r) {
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


    public int size() {
        return items.size();
    }

    public boolean offer(ActionHistory e) {
        if (!items.isEmpty() &&
            items.getLast().occurredAt() + 2_500 < e.occurredAt()) {
            items.clear();
        }
        return items.add(e);
    }

    public ActionHistory poll() {
        return items.isEmpty() ? null : items.removeFirst();
    }

    public ActionHistory peek() {
        return items.isEmpty() ? null : items.getFirst();
    }

}
