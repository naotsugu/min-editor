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
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * An abstract implementation of the {@code Content} interface, designed as an adapter
 * that delegates all method calls to an internal {@code Content} instance.
 * This class allows easy extension of existing implementations of the {@code Content} interface.
 * @author Naotsugu Kobayashi
 */
public abstract class ContentAdapter implements Content {

    @Override
    public Point insert(Point point, String text) {
        return pear().insert(point, text);
    }

    @Override
    public List<Point> insert(List<Point> points, String text) {
        return pear().insert(points, text);
    }

    @Override
    public String delete(Point point) {
        return pear().delete(point);
    }

    @Override
    public List<Point> delete(List<Point> points) {
        return pear().delete(points);
    }

    @Override
    public Point backspace(Point point) {
        return pear().backspace(point);
    }

    @Override
    public List<Point> backspace(List<Point> points) {
        return pear().backspace(points);
    }

    @Override
    public Point replace(Point start, Point end, String text) {
        return pear().replace(start, end, text);
    }

    @Override
    public List<Point.Range> replace(List<Point.Range> ranges, List<Function<String, String>> fun) {
        return pear().replace(ranges, fun);
    }

    @Override
    public List<Point> undo() {
        return pear().undo();
    }

    @Override
    public List<Point> redo() {
        return pear().redo();
    }

    @Override
    public String getText(int row) {
        return pear().getText(row);
    }

    @Override
    public String getText(Point start, Point end) {
        return pear().getText(start, end);
    }

    @Override
    public int rows() {
        return pear().rows();
    }

    @Override
    public Optional<Path> path() {
        return pear().path();
    }

    @Override
    public Optional<FileTime> lastModifiedTime() {
        return pear().lastModifiedTime();
    }

    @Override
    public boolean readonly() {
        return pear().readonly();
    }

    @Override
    public void save(Path path) {
        pear().save(path);
    }

    @Override
    public void reload() {
        pear().reload();
    }

    @Override
    public void reloadWith(Charset charset) {
        pear().reloadWith(charset);
    }

    @Override
    public void write(Path path) {
        pear().write(path);
    }

    @Override
    public void close() {
        pear().close();
    }

    @Override
    public Point insertFlush(Point point, String text) {
        return pear().insertFlush(point, text);
    }

    @Override
    public void clearFlush() {
        pear().clearFlush();
    }

    @Override
    public Find find() {
        return pear().find();
    }

    @Override
    public <R> R query(Query<R> query) {
        return pear().query(query);
    }

    abstract protected Content pear();
}
