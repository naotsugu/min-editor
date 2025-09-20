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

import com.mammb.code.editor.core.model.Sessions;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a session containing information such as file paths,
 * modification times, caret positions, and other metadata.
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
     * Get the alt name.
     * @return the alt name
     */
    String altName();

    /**
     * Get the charset.
     * @return the charset
     */
    Charset charset();

    /**
     * Checks whether the session is read-only.
     * @return {@code true} if the session is read-only, {@code false} otherwise
     */
    boolean readonly();

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
     * Get the preferred name of the session.
     * @return the preferred name
     */
    default String preferredName() {
        if (!altName().isBlank()) {
            return altName();
        }
        if (path() != null) {
            return path().getFileName().toString();
        }
        return "";
    }

    /**
     * Checks whether the session is empty. A session is considered empty
     * if it does not have a path and does not have an alternate path.
     * @return {@code true} if the session is empty, {@code false} otherwise
     */
    default boolean isEmpty() {
        return !hasPath() && !hasAltPath();
    }

    /**
     * Get the session as a read-only session.
     * @return a read-only session
     */
    default Session asReadonly() {
        return record(
            path(),
            lastModifiedTime(),
            altPath(),
            altName(),
            charset(),
            true,
            topLine(),
            lineWidth(),
            caretRow(),
            caretCol(),
            timestamp());
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
            .add("altName=" + ((altName() == null) ? "" : altName()))
            .add("charset=" + ((charset() == null) ? "" : charset()))
            .add("readonly=" + readonly())
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
        return record(null, null, null, "", null, false, 0, 0, 0, 0, System.currentTimeMillis());
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
        return record(
            existsValue(map, "path") ? Path.of(map.get("path")) : null,
            existsValue(map, "lastModifiedTime") ? FileTime.fromMillis(Long.parseLong(map.get("lastModifiedTime"))) : null,
            existsValue(map, "altPath") ? Path.of(map.get("altPath")) : null,
            map.getOrDefault("altName", ""),
            existsValue(map, "charset") ? Charset.forName(map.get("charset")) : null,
            Boolean.parseBoolean(map.getOrDefault("readonly", "false")),
            Integer.parseInt(map.getOrDefault("topLine", "0")),
            Integer.parseInt(map.getOrDefault("lineWidth", "0")),
            Integer.parseInt(map.getOrDefault("caretRow", "0")),
            Integer.parseInt(map.getOrDefault("caretCol", "0")),
            Long.parseLong(map.getOrDefault("timestamp", "0")));
    }

    static Session altOf(Path path, String name) {
        return record(
            null, null, path, name,
            StandardCharsets.UTF_8, false, 0, 0, 0, 0,
            System.currentTimeMillis());
    }

    /**
     * Create a new {@link Session}.
     * @param path the path
     * @return a new {@link Session}
     */
    static Session of(Path path) {
        return record(
            Files.exists(path) ? path : null,
            Files.lastModifiedTime(path),
            null,
            "",
            null,
            false,
            0, 0, 0, 0,
            System.currentTimeMillis());
    }

    /**
     * Create a new {@link Session}.
     * @param path the path
     * @param lastModifiedTime the last modified time
     * @param altPath the alt path
     * @param altName the alt name
     * @param charset the charset
     * @param readonly the readonly
     * @param topLine the line number at the top of the screen
     * @param lineWidth the width of line wrap characters
     * @param caretRow the row index at the caret
     * @param caretCol the column index at the caret
     * @return a new {@link Session}
     */
    static Session of(Path path, FileTime lastModifiedTime,
            Path altPath, String altName,
            Charset charset, boolean readonly,
            int topLine, int lineWidth, int caretRow, int caretCol) {
        return record(
            path,
            lastModifiedTime,
            altPath,
            altName,
            charset,
            readonly,
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

    private static Session record(
        Path path,
        FileTime lastModifiedTime,
        Path altPath,
        String altName,
        Charset charset,
        boolean readonly,
        int topLine,
        int lineWidth,
        int caretRow,
        int caretCol,
        long timestamp) {

        record SessionRecord(Path path, FileTime lastModifiedTime, Path altPath, String altName,
            Charset charset, boolean readonly, int topLine, int lineWidth, int caretRow, int caretCol, long timestamp) implements Session {
            public SessionRecord {
                altName = Objects.requireNonNullElse(altName, "");
            }
        }

        return new SessionRecord(path, lastModifiedTime, altPath, altName, charset, readonly, topLine, lineWidth, caretRow, caretCol, timestamp);

    }

    interface Transformer {
        Session apply(Context ctx, Content content);
    }

    static Transformer diff(Path path, boolean withoutFold) {
        return new Sessions.Diff(path, withoutFold);
    }

}
