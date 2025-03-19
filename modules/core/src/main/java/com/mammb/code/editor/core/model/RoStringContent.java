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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Stream;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FindSpec;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Point.PointLen;
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
        this.stat = DocumentStat.of(path, rowLimit);
        this.stringList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        try (var reader = Files.newBufferedReader(path, stat.charset())) {
            int c;
            while ((c = reader.read()) > 0) {
                sb.append((char) c);
                if (c == '\n') {
                    stringList.add(sb.toString());
                    sb.setLength(0);
                    if (stringList.size() >= rowLimit) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!sb.isEmpty()) {
            stringList.add(sb.toString());
        }
        if (stringList.isEmpty()) {
            stringList.add("");
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
    public List<PointLen> findAll(FindSpec findSpec) {
        // TODO case-sensitive regexp
        List<PointLen> ret = new ArrayList<>();
        for (int row = 0; row < stringList.size(); row++) {
            String rowText = stringList.get(row);
            for (int col = 0; col < rowText.length(); col++) {
                int index = rowText.indexOf(findSpec.pattern(), col);
                if (index < 0) {
                    break;
                }
                ret.add(PointLen.of(row, index, findSpec.pattern().length()));
                col = (index + findSpec.pattern().length() - 1);
            }
        }
        return ret;
    }

    @Override
    public Optional<PointLen> findNext(Point base, FindSpec findSpec) {
        // TODO case-sensitive regexp
        for (int row = base.row(); row < stringList.size(); row++) {
            String rowText = stringList.get(row);
            int col = (row == base.row()) ? base.col() : 0;
            int index = rowText.indexOf(findSpec.pattern(), col);
            if (index >= 0) {
                return Optional.of(PointLen.of(row, index, findSpec.pattern().length()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<PointLen> findPrev(Point base, FindSpec findSpec) {
        // TODO case-sensitive regexp
        for (int row = base.row(); row >= 0; row--) {
            String rowText = stringList.get(row);
            int col = (row == base.row()) ? base.col() : rowText.length();
            int index = rowText.substring(0, col).lastIndexOf(findSpec.pattern());
            if (index >= 0) {
                return Optional.of(PointLen.of(row, index, findSpec.pattern().length()));
            }
        }
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
