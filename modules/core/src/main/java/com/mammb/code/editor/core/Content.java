/*
 * Copyright 2022-2024 the original author or authors.
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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import com.mammb.code.editor.core.Point.Range;
import com.mammb.code.editor.core.Point.PointLen;
import com.mammb.code.editor.core.model.RoStringContent;
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
     * Save the target content to a file.
     * @param path the path of file
     */
    void save(Path path);

    /**
     * Insert the text being edited with IME.
     * @param point the point at insertion
     * @param text the text being edited with IME
     * @return caret point after insert
     */
    Point insertFlush(Point point, String text);

    /**
     * Clear the flushes(text being edited with IME).
     */
    void clearFlush();

    /**
     * Find all.
     * @param findSpec the find spec
     * @return the search results
     */
    List<PointLen> findAll(FindSpec findSpec);

    /**
     * Find next.
     * @param base the base point
     * @param findSpec the findSpec
     * @return the search result
     */
    Optional<PointLen> findNext(Point base, FindSpec findSpec);

    /**
     * Find previous.
     * @param base the base point
     * @param findSpec the findSpec
     * @return the search result
     */
    Optional<PointLen> findPrev(Point base, FindSpec findSpec);

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
     * @param traverseCallback  the coll back function
     * @return a new {@link Content}
     */
    static Content of(Path path, Function<Long, Boolean> traverseCallback) {
        return new TextEditContent(path, traverseCallback);
    }

    /**
     * Create a new partial {@link Content}.
     * @param path the specified path
     * @return a new partial {@link Content}
     */
    static Content readOnlyPartOf(Path path) {
        return new RoStringContent(path);
    }

}
