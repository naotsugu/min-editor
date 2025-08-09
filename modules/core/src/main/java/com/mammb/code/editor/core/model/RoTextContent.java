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
import com.mammb.code.editor.core.Find;
import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * RoTextContent is an immutable implementation of the {@code Content} interface.
 * This class delegates all read operations to an underlying {@code Content}
 * instance while suppressing any modification operations.
 * @author Naotsugu Kobayashi
 */
public class RoTextContent implements Content {

    /** The pear content. */
    private final Content pear;

    /**
     * Constructor for RoTextContent that wraps a given Content instance.
     * @param pear the underlying Content instance to be wrapped
     */
    public RoTextContent(Content pear) {
        this.pear = pear;
    }

    /**
     * Creates a new {@code RoTextContent} instance from a given file path.
     * This method wraps the content of the file located at the specified path
     * inside a {@code TextEditContent} and then constructs a {@code RoTextContent}
     * to provide an immutable representation of the content.
     *
     * @param path the {@code Path} of the file to be wrapped in a {@code RoTextContent} instance
     * @return a new {@code RoTextContent} instance representing the immutable content of the file
     */
    public static RoTextContent of(Path path) {
        return new RoTextContent(new TextEditContent(path));
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
    public static RoTextContent of(Path path, int rowLimit) {
        return new RoTextContent(new TextEditContent(read(path, rowLimit), path.getFileName().toString()));
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
    public List<Point.Range> replace(List<Point.Range> ranges, Function<String, String> fun) {
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
    public String getText(int row) {
        return pear.getText(row);
    }

    @Override
    public String getText(Point start, Point end) {
        return pear.getText(start, end);
    }

    @Override
    public int rows() {
        return pear.rows();
    }

    @Override
    public Optional<Path> path() {
        return pear.path();
    }

    @Override
    public boolean readonly() {
        return true;
    }

    @Override
    public void save(Path path) {
    }

    @Override
    public void reload() {
        pear.reload();
    }

    @Override
    public void reloadWith(Charset charset) {
        pear.reloadWith(charset);
    }

    @Override
    public void write(Path path) {
        pear.write(path);
    }

    @Override
    public void close() {
        pear.close();
    }

    @Override
    public Point insertFlush(Point point, String text) {
        return point;
    }

    @Override
    public void clearFlush() {
    }

    @Override
    public Find find() {
        return pear.find();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.Modified _ -> (R) Boolean.FALSE;
            case QueryRecords.ModelName q  -> (R) readonlyName(pear.query(q));
            default -> pear.query(query);
        };
    }

    private Name readonlyName(Name name) {
        record NameRecord(String canonical, String plain, String contextual) implements Name { }
        return new NameRecord(name.canonical(), name.plain(), "[" + name.plain() + "]");
    }

    private static byte[] read(Path path, int rowLimit) {
        var output = new ByteArrayOutputStream();
        var buffer = new byte[1024 * 64];
        try (var in = Files.newInputStream(path, StandardOpenOption.READ)) {
            int n;
            while ((n = in.read(buffer)) > 0) {
                for (int i = 0; i < n; i++) {
                    if (buffer[i] == '\n') {
                        rowLimit--;
                        if (rowLimit <= 0) {
                            output.write(buffer, 0, i);
                            return output.toByteArray();
                        }
                    }
                }
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
