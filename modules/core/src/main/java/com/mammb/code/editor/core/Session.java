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
package com.mammb.code.editor.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

/**
 * The Session.
 * @author Naotsugu Kobayashi
 */
public interface Session {

    /**
     * Get the path.
     * @return the path
     */
    Path path();

    /**
     * Get the last modified time.
     * @return the last modified time
     */
    FileTime lastModifiedTime();

    /**
     * The line number at the top of the screen.
     * @return line number at the top of the screen
     */
    int topLine();

    /**
     * The width of line wrap characters.
     * @return the width of line wrap characters
     */
    int lineWidth();

    /**
     * Get the row index at the caret(zero-origin).
     * @return the row index at the caret
     */
    int caretRow();

    /**
     * Get the column index at the caret.
     * @return the column index at the caret
     */
    int caretCol();


    /**
     * Get the timestamp of this session in milliseconds.
     * @return the timestamp of this session in milliseconds
     */
    long timestamp();

    default boolean hasPath() {
        return path() != null;
    }

    default boolean existsFile() {
        return Files.exists(path());
    }

    static Session of(Path path) {
        if (path != null && Files.exists(path)) {
            try {
                return new SessionRecord(path, Files.getLastModifiedTime(path), 0, 0, 0, 0, System.currentTimeMillis());
            } catch (Exception e) { throw new RuntimeException(e); }
        } else {
            return new SessionRecord(null, null, 0, 0, 0, 0, System.currentTimeMillis());
        }
    }

    /**
     * The SessionRecord.
     * @param path the path
     * @param lastModifiedTime the last modified time
     * @param topLine the line number at the top of the screen
     * @param lineWidth the width of line wrap characters
     * @param caretRow the row index at the caret
     * @param caretCol the column index at the caret
     * @param timestamp the timestamp of this session
     * @author Naotsugu Kobayashi
     */
    record SessionRecord(
        Path path,
        FileTime lastModifiedTime,
        int topLine,
        int lineWidth,
        int caretRow,
        int caretCol,
        long timestamp) implements Session {

    }

    /**
     * The session history.
     */
    class SessionHistory {

        /** The backward queue. */
        private final Deque<Session> left = new ArrayDeque<>();
        /** The forward queue. */
        private final Deque<Session> right = new ArrayDeque<>();

        public void push(Session session) {
            if (session.hasPath() && (left.isEmpty() || !Objects.equals(left.peek().path(), session.path()))) {
                left.push(session);
                right.clear();
            }
        }

        public void updateCurrent(Session session) {
            if (!session.hasPath()) return;
            if (!left.isEmpty() && Objects.equals(left.peek().path(), session.path())) {
                left.remove();
            }
            left.push(session);
        }

        public Optional<Session> backward() {
            if (left.size() < 2) {
                return Optional.empty();
            }
            right.add(left.poll());
            Session s = left.peek();
            return (s != null && s.existsFile()) ? Optional.of(s) : backward();
        }

        public Optional<Session> forward() {
            if (right.isEmpty()) {
                return Optional.empty();
            }
            left.push(right.pollLast());
            Session s = left.peek();
            return (s != null && s.existsFile()) ? Optional.of(s) : forward();
        }

    }

}
