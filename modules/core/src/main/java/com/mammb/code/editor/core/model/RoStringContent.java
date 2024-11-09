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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The read only string content.
 * @author Naotsugu Kobayashi
 */
public class RoStringContent implements Content {

    private final List<String> stringList;
    private final Path path;

    public RoStringContent(Path path) {
        this(path, 1000);
    }

    public RoStringContent(Path path, int limitLines) {
        this.path = path;

        try (Stream<String> stream = Files.lines(path)) {
            stringList = stream
                .map(s -> s + System.lineSeparator())
                .limit(limitLines)
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return points;
    }

    @Override
    public Point replace(Point start, Point end, String text) {
        return end;
    }

    @Override
    public List<Point> replace(List<Point.Range> ranges, Function<String, String> fun) {
        return ranges.stream().map(Point.Range::end).toList();
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
    public void save(Path path) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public Point insertFlush(Point point, String text) {
        return point;
    }

    @Override
    public void clearFlush() {
    }

    @Override
    public String getText(int row) {
        return stringList.get(Math.clamp(row, 0, stringList.size() - 1));
    }

    @Override
    public String getText(Point start, Point end) {
        var sb = new StringBuilder();
        for (int i = start.row(); i <= end.row(); i++) {
            String row = getText(i);
            row = (i == end.row()) ? row.substring(0, end.col()) : row;
            row = (i == start.row()) ? row.substring(start.col()) : row;
            sb.append(row);
        }
        return sb.toString();
    }

    @Override
    public int rows() {
        return stringList.size();
    }

    @Override
    public Optional<Path> path() {
        return Optional.of(path);
    }

    @Override
    public List<Point> findAll(String text) {
        return List.of();
    }
}
