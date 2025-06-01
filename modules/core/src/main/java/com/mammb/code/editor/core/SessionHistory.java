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
package com.mammb.code.editor.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

/**
 * The session history.
 */
public class SessionHistory {

    /** The backward session queue. */
    private final Deque<Session> left = new ArrayDeque<>();

    /** The forward session queue. */
    private final Deque<Session> right = new ArrayDeque<>();

    private Deque<Session> select;

    public void push(Session session) {
        if (!session.hasPath()) return;
        boolean clearForward = (select == null);
        Deque<Session> sel = clearForward ? left : select;
        select = null;
        if (sel.isEmpty() || !Objects.equals(sel.peek().path(), session.path())) {
            sel.push(session);
            if (clearForward) {
                right.clear();
            }
        }
    }

    public Optional<Session> backward() {
        if (left.isEmpty()) {
            return Optional.empty();
        }
        select = right;
        return Optional.of(left.pop());
    }

    public Optional<Session> forward() {
        if (right.isEmpty()) {
            return Optional.empty();
        }
        select = left;
        return Optional.of(right.pop());
    }

}
