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
 * @author Naotsugu Kobayashi
 */
public class SessionHistory {

    /** The backward session queue. */
    private final Deque<Session> left = new ArrayDeque<>();

    /** The forward session queue. */
    private final Deque<Session> right = new ArrayDeque<>();

    /** The current session queue. */
    private Deque<Session> select;

    /**
     * Adds a new session to the appropriate session queue. If the session does not already
     * exist in the current queue, it is added. If the forward session queue is cleared
     * during the operation, the forward queue is also invalidated.
     * @param session the session to be added; must have a valid path
     */
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

    /**
     * Checks if there are any sessions available in the backward session queue.
     * @return {@code true} if the backward session queue is not empty
     */
    public boolean hasBackward() {
        return !left.isEmpty();
    }

    /**
     * Checks if there are any sessions available in the forward session queue.
     * @return {@code true} if the forward session queue is not empty
     */
    public boolean hasForward() {
        return !right.isEmpty();
    }

    /**
     * Moves to the previous session in the backward session queue and returns it if available.
     * If the backward session queue is empty, an empty {@link Optional} is returned.
     * @return an {@link Optional} containing the previous session if present,
     * or an empty {@link Optional} if the backward session queue is empty
     */
    public Optional<Session> backward() {
        if (left.isEmpty()) {
            return Optional.empty();
        }
        select = right;
        return Optional.of(left.pop());
    }

    /**
     * Moves to the next session in the forward session queue and returns it if available.
     * If the forward session queue is empty, an empty {@link Optional} is returned.
     * @return an {@link Optional} containing the next session if present,
     *         or an empty {@link Optional} if the forward session queue is empty
     */
    public Optional<Session> forward() {
        if (right.isEmpty()) {
            return Optional.empty();
        }
        select = left;
        return Optional.of(right.pop());
    }

}
