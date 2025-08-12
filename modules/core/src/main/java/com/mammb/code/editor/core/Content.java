/*
 * Copyright 2022-2025 the original author or authors.
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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import com.mammb.code.editor.core.Point.Range;
import com.mammb.code.editor.core.model.RoTextContent;
import com.mammb.code.editor.core.model.TextEditContent;

/**
 * The content.
 * @author Naotsugu Kobayashi
 */
public interface Content {

    /**
     * Insert specified text.
     * @param point the insert point
     * @param text the text
     * @return the caret position after insertion
     */
    Point insert(Point point, String text);

    /**
     * Insert specific text at multiple caret points.
     * @param points the insert point list
     * @param text the text
     * @return the caret position after insertion
     */
    List<Point> insert(List<Point> points, String text);

    /**
     * Deletes the character at a specific caret point.
     * @param point a specific caret point
     * @return the deleted characters
     */
    String delete(Point point);

    /**
     * Deletes the character at multiple caret points.
     * @param points the deletes point list
     * @return the caret position after deletion
     */
    List<Point> delete(List<Point> points);

    /**
     * Backspace the character at a specific caret point.
     * @param point a specific caret point
     * @return the caret position after backspace
     */
    Point backspace(Point point);

    /**
     * Backspace the character at multiple caret points.
     * @param points the deletes point list
     * @return the caret position after backspace
     */
    List<Point> backspace(List<Point> points);

    /**
     * Replace a specific range of text with the specified text.
     * @param start the start point
     * @param end  the start point(inclusive)
     * @param text the specified text
     * @return the caret position after replacement
     */
    Point replace(Point start, Point end, String text);

    /**
     * Replace the specific ranges of text with the specified function.
     * @param ranges the specific ranges
     * @param fun the specified function
     * @return the caret position after replacement
     */
    List<Range> replace(List<Range> ranges, Function<String, String> fun);

    /**
     * Undo.
     * @return the undo position
     */
    List<Point> undo();

    /**
     * Redo.
     * @return the redo position
     */
    List<Point> redo();

    /**
     * Get the text of a specific row.
     * @param row a specific row
     * @return the text of a specific row
     */
    String getText(int row);

    /**
     * Get the specific ranges of text.
     * @param start the start point
     * @param end  the start point(inclusive)
     * @return the specific ranges of text
     */
    String getText(Point start, Point end);

    /**
     * Get the size of rows.
     * @return the size of rows
     */
    int rows();

    /**
     * Get the content path.
     * @return the content path
     */
    Optional<Path> path();

    /**
     * Checks whether the content is read-only.
     * @return {@code true} if the content is read-only, {@code false} otherwise
     */
    boolean readonly();

    /**
     * Save the target content to a file.
     * @param path the path of a file
     */
    void save(Path path);

    /**
     * Reloads the content, refreshing its state to reflect any external changes.
     */
    void reload();

    /**
     * Reloads the content, refreshing its state to reflect any external changes using the specified character set.
     * @param charset the character set to be used for reloading the content
     */
    void reloadWith(Charset charset);

    /**
     * Writes the contents to the specified path.
     * This method is intended for backup path creation and other uses.
     * @param path the specified path
     */
    void write(Path path);

    /**
     * Closes the content and releases associated resources.
     */
    void close();

    /**
     * Insert the text being edited with IME.
     * @param point the point at insertion
     * @param text the text being edited with IME
     * @return caret point after insert
     */
    Point insertFlush(Point point, String text);

    /**
     * Clear the flushes (text being edited with IME).
     */
    void clearFlush();

    /**
     * Get the find.
     * @return the find
     */
    Find find();

    /**
     * Execute the query.
     * @param query the query
     * @return the query result
     * @param <R> the type of result
     */
    <R> R query(Query<R> query);

    /**
     * Create a new {@link Content}.
     * @return a new {@link Content}
     */
    static Content of() {
        return new TextEditContent();
    }

    /**
     * Create a new {@link Content} from the specified path.
     * @param path the specified path
     * @return a new {@link Content}
     */
    static Content of(Path path) {
        return new TextEditContent(path);
    }

    /**
     * Create a new {@link Content} from the specified path with the coll back.
     * @param path the specified path
     * @param consumer the progress callback
     * @return a new {@link Content}
     */
    static Content of(Path path, Consumer<Long> consumer) {
        return new TextEditContent(path, consumer);
    }

    /**
     * Creates a new {@link Content} from the specified byte array.
     * @param bytes the byte array representing the content data
     * @param contentName the content name
     * @return a new {@link Content} instance initialized with the provided bytes
     */
    static Content of(byte[] bytes, String contentName) {
        return new TextEditContent(bytes, contentName);
    }

    /**
     * Create a new read-only {@link Content} from the specified path.
     * @param path the specified path
     * @return a new read-only {@link Content}
     */
    static Content readonlyOf(Path path) {
        return RoTextContent.of(path);
    }

    /**
     * Create a new read-only partial {@link Content} from the specified path.
     * @param path the specified path
     * @return a new read-only partial {@link Content}
     */
    static Content readonlyPartOf(Path path) {
        return RoTextContent.of(path, 5_000);
    }

    /**
     * Creates a {@link Content} instance based on the given session's paths.
     * @param session the session from which to retrieve content details
     * @return the content created from the session paths or an empty content if no valid path is found
     */
    static Content of(Session session) {
        Content content;
        if (Files.exists(session.altPath())) {
            content = Content.of(Files.readAllBytes(session.altPath()),
                session.hasPath() ? session.path().getFileName().toString() : null);
        } else if (session.hasPath() && Files.exists(session.path())) {
            content = Content.of(session.path());
        } else {
            content = Content.of();
        }
        return session.readonly() ? new RoTextContent(content) : content;
    }

}
