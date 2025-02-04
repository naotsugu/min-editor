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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.model.QueryRecords.*;
import com.mammb.code.piecetable.DocumentStat;

/**
 * The read only string content.
 * This content is intended to provide a temporary content implementation,
 * such as when loading large files.
 *
 * @author Naotsugu Kobayashi
 */
public class RoStringContent implements Content {

    /** The content. */
    private final List<String> stringList;
    /** The content path. */
    private final Path path;
    /** The document statistics. */
    private final DocumentStat stat;

    /**
     * Constructor.
     * @param path the content path
     */
    public RoStringContent(Path path) {
        this(path, 5_000);
    }

    /**
     * Constructor.
     * @param path the content path
     * @param rowLimit the size of row Limit
     */
    public RoStringContent(Path path, int rowLimit) {
        this.path = path;

        stat = DocumentStat.of(path, rowLimit);
        String rowEnding = stat.rowEnding().str();

        try (Stream<String> stream = Files.lines(path, stat.charset())) {
            stringList = stream
                .map(s -> s + rowEnding)
                .limit(rowLimit)
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
        List<Point> ret = new ArrayList<>();
        for (int row = 0; row < stringList.size(); row++) {
            String rowText = stringList.get(row);
            for (int col = 0; col < rowText.length(); col++) {
                int index = rowText.indexOf(text, col);
                if (index < 0) {
                    break;
                }
                ret.add(Point.of(row, index));
                col = (index + text.length() - 1);
            }
        }
        return ret;
    }

    @Override
    public Optional<Point> findNext(Point base, String text) {
        // TODO
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case RowEndingSymbol q -> (R) stat.rowEnding().toString();
            case CharsetSymbol q -> (R) stat.charset().toString();
            case Modified q -> (R) Boolean.FALSE;
            case Bom q -> (R) stat.bom();
            default -> null;
        };
    }

}
