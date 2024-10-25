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

import com.mammb.code.piecetable.TextEdit;
import com.mammb.code.editor.core.Caret.Point;
import com.mammb.code.editor.core.Caret.PointRec;
import com.mammb.code.editor.core.Caret.Range;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The content.
 * @author Naotsugu Kobayashi
 */
public interface Content {

    Point insert(Point point, String text);
    List<Point> insert(List<Point> points, String text);
    String delete(Point point);
    List<Point> delete(List<Point> points);
    Point backspace(Point point);
    List<Point> backspace(List<Point> points);
    Point replace(Point start, Point end, String text);
    List<Point> replace(List<Range> ranges, String text);

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

    String getText(int row);
    String getText(Point start, Point end);
    int rows();
    Optional<Path> path();
    void save(Path path);
    boolean isModified();
    Point insertFlush(Point point, String text);
    void clearFlush();
    List<Point> findAll(String text);

    static Content of() {
        return new ContentImpl();
    }
    static Content of(Path path) {
        return new ContentImpl(path);
    }


    class ContentImpl implements Content {
        private final TextEdit edit;
        private final List<PointText> flushes = new ArrayList<>();

        public ContentImpl() {
            this.edit = TextEdit.of();
        }
        public ContentImpl(Path path) {
            this.edit = TextEdit.of(path);
        }

        @Override
        public Point insert(Point point, String text) {
            var pos = edit.insert(point.row(), point.col(), text);
            return new PointRec(pos.row(), pos.col());
        }

        @Override
        public List<Point> insert(List<Point> points, String text) {
            var pos = edit.insert(points.stream()
                    .map(p -> new TextEdit.Pos(p.row(), p.col())).toList(), text);
            return pos.stream().map(p -> (Point) new PointRec(p.row(), p.col())).toList();
        }

        @Override
        public String delete(Point point) {
            return edit.delete(point.row(), point.col());
        }

        @Override
        public List<Point> delete(List<Point> points) {
            var pos = edit.delete(points.stream()
                    .map(p -> new TextEdit.Pos(p.row(), p.col())).toList());
            return pos.stream().map(p -> (Point) new PointRec(p.row(), p.col())).toList();
        }

        @Override
        public Point backspace(Point point) {
            var pos = edit.backspace(point.row(), point.col());
            return new PointRec(pos.row(), pos.col());
        }

        @Override
        public List<Point> backspace(List<Point> points) {
            var pos = edit.backspace(points.stream()
                    .map(p -> new TextEdit.Pos(p.row(), p.col())).toList());
            return pos.stream().map(p -> (Point) new PointRec(p.row(), p.col())).toList();
        }

        @Override
        public Point replace(Point start, Point end, String text) {
            var pos = edit.replace(start.row(), start.col(), end.row(), end.col(), text);
            return new PointRec(pos.row(), pos.col());
        }

        @Override
        public List<Point> replace(List<Range> ranges, String text) {
            // TODO transaction replace
            return ranges.stream().sorted(Comparator.reverseOrder())
                    .map(range -> edit.replace(
                            range.min().row(), range.min().col(),
                            range.max().row(), range.max().col(),
                            text))
                    .map(pos -> new PointRec(pos.row(), pos.col()))
                    .map(Point.class::cast)
                    .toList();
        }

        @Override
        public List<Point> undo() {
            return edit.undo().stream().map(p -> Point.of(p.row(), p.col())).toList();
        }

        @Override
        public List<Point> redo() {
            return edit.redo().stream().map(p -> Point.of(p.row(), p.col())).toList();
        }

        @Override
        public String getText(int row) {
            if (!flushes.isEmpty()) {
                var sb = new StringBuilder(edit.getText(row));
                flushes.stream().filter(p -> p.point.row() == row)
                        .forEach(p -> sb.insert(p.point().col(), p.text()));
                return sb.toString();
            } else {
                return edit.getText(row);
            }
        }

        @Override
        public String getText(Point start, Point end) {
            if (!flushes.isEmpty()) {
                var sb = new StringBuilder();
                for (int i = start.row(); i <= end.row(); i++) {
                    String row = getText(i);
                    row = (i == end.row()) ? row.substring(0, end.col()) : row;
                    row = (i == start.row()) ? row.substring(start.col()) : row;
                    sb.append(row);
                }
                return sb.toString();
            } else {
                return edit.getText(start.row(), start.col(), end.row(), end.col());
            }
        }

        @Override
        public int rows() {
            return edit.rows();
        }

        @Override
        public Optional<Path> path() {
            return Optional.ofNullable(edit.path());
        }

        @Override
        public void save(Path path) {
            edit.save(path);
        }

        @Override
        public boolean isModified() {
            edit.flush();
            return edit.hasUndoRecord();
        }

        @Override
        public Point insertFlush(Point point, String text) {
            flushes.add(new PointText(point, text));
            return new PointRec(point.row(), point.col() + text.length());
        }

        @Override
        public void clearFlush() {
            flushes.clear();
        }

        @Override
        public List<Point> findAll(String text) {
            var founds = edit.findAll(text);
            return founds.stream()
                    .map(found -> (Point) new PointRec(found.row(), found.col()))
                    .toList();
        }

        record PointText(Point point, String text) {}

    }
}
