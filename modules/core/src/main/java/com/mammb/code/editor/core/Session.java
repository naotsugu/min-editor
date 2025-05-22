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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Represents a session containing information such as file paths,
 * modification times, caret positions, and other metadata.
 *
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
     * Get the alt path.
     * @return the alt path
     */
    Path altPath();

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
     * Get the row index at the caret (zero-origin).
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

    /**
     * Determines if the current session has an associated path.
     * @return {@code true} if the path is not {@code null}, otherwise {@code false}
     */
    default boolean hasPath() {
        return path() != null;
    }

    /**
     * Determines if there is an alternate path.
     * @return {@code true} if the alternate path is not {@code null}, otherwise {@code false}
     */
    default boolean hasAltPath() {
        return altPath() != null;
    }

    /**
     * Get the session as a string.
     * @return session string
     */
    default String asString() {
        return new StringJoiner(File.pathSeparator, "[", "]")
            .add("path=" + ((path() == null) ? "" : path()))
            .add("lastModifiedTime=" + ((lastModifiedTime() == null) ? "" : lastModifiedTime().toMillis()))
            .add("altPath=" + ((altPath() == null) ? "" : altPath()))
            .add("topLine=" + topLine())
            .add("lineWidth=" + lineWidth())
            .add("caretRow=" + caretRow())
            .add("caretCol=" + caretCol())
            .add("timestamp=" + timestamp())
            .toString();
    }

    /**
     * Creates an empty {@code Session} instance with default values.
     * @return a new {@code Session} instance with all fields initialized to default values
     */
    static Session empty() {
        return new SessionRecord(null, null, null, 0, 0, 0, 0, System.currentTimeMillis());
    }

    /**
     * Create a new {@link Session} from the source string.
     * @param string the source string
     * @return a new {@link Session}
     */
    static Session valueOf(String string) {
        if (string == null || string.length() <= 2) return null;
        Map<String, String> map = new HashMap<>();
        for (String entry : string.substring(1, string.length() - 1).split(File.pathSeparator)) {
            var split = entry.split("=", 2);
            if (split.length > 1) {
                map.put(split[0], split[1]);
            }
        }
        return new SessionRecord(
            existsValue(map, "path") ? Path.of(map.get("path")) : null,
            existsValue(map, "lastModifiedTime") ? FileTime.fromMillis(Long.parseLong(map.get("lastModifiedTime"))) : null,
            existsValue(map, "altPath") ? Path.of(map.get("altPath")) : null,
            Integer.parseInt(map.getOrDefault("topLine", "0")),
            Integer.parseInt(map.getOrDefault("lineWidth", "0")),
            Integer.parseInt(map.getOrDefault("caretRow", "0")),
            Integer.parseInt(map.getOrDefault("caretCol", "0")),
            Long.parseLong(map.getOrDefault("timestamp", "0")));
    }

    /**
     * Create a new {@link Session}.
     * @param path the path
     * @return a new {@link Session}
     */
    static Session of(Path path) {
        try {
            boolean pathExists = (path != null && Files.exists(path));
            return new SessionRecord(
                pathExists ? path : null,
                pathExists ? Files.getLastModifiedTime(path) : null,
                null,
                0, 0, 0, 0,
                System.currentTimeMillis());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new {@link Session}.
     * @param path the path
     * @param lastModifiedTime the last modified time
     * @param topLine the line number at the top of the screen
     * @param lineWidth the width of line wrap characters
     * @param caretRow the row index at the caret
     * @param caretCol the column index at the caret
     * @return a new {@link Session}
     */
    static Session of(Path path, FileTime lastModifiedTime, Path altPath,
        int topLine, int lineWidth, int caretRow, int caretCol) {
        return new SessionRecord(
            path,
            lastModifiedTime,
            altPath,
            topLine,
            lineWidth,
            caretRow,
            caretCol,
            System.currentTimeMillis());
    }

    private static boolean existsValue(Map<String, String> map, String key) {
        if (!map.containsKey(key)) return false;
        var val = map.get(key);
        return val != null && !val.isBlank();
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
        Path altPath,
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

}
