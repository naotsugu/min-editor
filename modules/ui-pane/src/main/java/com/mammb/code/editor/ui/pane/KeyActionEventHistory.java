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
public class KeyActionEventHistory {

    /** The items. */
    private final List<KeyActionEvent> items;

    /**
     * Constructor.
     */
    public KeyActionEventHistory() {
        this.items = new ArrayList<>();
    }

    public List<KeyActionEvent> repetition() {
        if (items.size() < 2) {
            return Collections.emptyList();
        }

        int mid = items.size() / 2;
        for (int i = mid; mid > 1; mid--) {
            int index = items.size() - i;
            List<KeyActionEvent> l = items.subList(index - i, i);
            List<KeyActionEvent> r = items.subList(i, items.size());
            if (equals(l, r)) {
                return r;
            }
        }

        return Collections.emptyList();
    }


    private boolean equals(List<KeyActionEvent> l, List<KeyActionEvent> r) {
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

    public boolean offer(KeyActionEvent e) {
        if (!items.isEmpty() &&
            items.getLast().occurredAt() + 2_500 < e.occurredAt()) {
            items.clear();
        }
        return items.add(e);
    }

    public KeyActionEvent poll() {
        return items.isEmpty() ? null : items.removeFirst();
    }

    public KeyActionEvent peek() {
        return items.isEmpty() ? null : items.getFirst();
    }

}
