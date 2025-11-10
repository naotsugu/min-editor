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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/**
 * RoTextContent is an immutable implementation of the {@code Content} interface.
 * This class delegates all read operations to an underlying {@code Content}
 * instance while suppressing any modification operations.
 * @author Naotsugu Kobayashi
 */
public class ReadonlyContent extends ContentAdapter {

    /** The pear content. */
    private final Content pear;

    /**
     * Constructor for RoTextContent that wraps a given Content instance.
     * @param pear the underlying Content instance to be wrapped
     */
    public ReadonlyContent(Content pear) {
        this.pear = pear;
    }

    /**
     * Creates a new {@code RoTextContent} instance from a given file path with a specified row limit.
     * This method reads the content from the specified file path, limiting it to the provided number of rows,
     * wraps the content in a {@code TextEditContent}, and constructs a {@code RoTextContent} to represent
     * an immutable view of the content.
     *
     * @param path the {@code Path} of the file to be read and wrapped in a {@code RoTextContent} instance
     * @param rowLimit the maximum number of rows to be read from the file
     * @return a new {@code RoTextContent} instance representing the immutable content of the file with the specified row limit
     */
    public static ReadonlyContent of(Path path, int rowLimit) {
        Content content = new TextEditContent(Files.read(path, rowLimit));
        return new ReadonlyContent(content);
    }

    @Override
    public Point insert(Point point, String text) {
        return point;
    }

    @Override
    public List<Point> insert(List<Point> points, String text) {
        return points;
    }

    @Override
    public String delete(Point point) {
        return "";
    }

    @Override
    public List<Point> delete(List<Point> points) {
        return List.of();
    }

    @Override
    public Point backspace(Point point) {
        return point;
    }

    @Override
    public List<Point> backspace(List<Point> points) {
        return List.of();
    }

    @Override
    public Point replace(Point start, Point end, String text) {
        return end;
    }

    @Override
    public List<Point.Range> replace(List<Point.Range> ranges, List<Function<String, String>> fun) {
        return ranges;
    }

    @Override
    public List<Point> undo() {
        return List.of();
    }

    @Override
    public List<Point> redo() {
        return List.of();
    }

    @Override
    public boolean readonly() {
        return true;
    }

    @Override
    public void save(Path path) {
    }

    @Override
    public Point insertFlush(Point point, String text) {
        return point;
    }

    @Override
    public void clearFlush() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.Modified _ -> (R) Boolean.FALSE;
            case QueryRecords.ModelName q  -> (R) readonlyName(super.query(q));
            default -> super.query(query);
        };
    }

    @Override
    protected Content pear() {
        return pear;
    }

    /**
     * Wraps the specified name with a readonly name.
     * @param name the specified name
     * @return the wrapped name
     */
    private Name readonlyName(Name name) {
        return Name.of(name.canonical(), name.plain(), "[" + name.plain() + "]");
    }

}
