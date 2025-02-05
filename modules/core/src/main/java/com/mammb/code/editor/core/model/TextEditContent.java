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
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.model.QueryRecords.Bom;
import com.mammb.code.editor.core.model.QueryRecords.CharsetSymbol;
import com.mammb.code.editor.core.model.QueryRecords.ContentPath;
import com.mammb.code.editor.core.model.QueryRecords.Modified;
import com.mammb.code.editor.core.model.QueryRecords.RowEndingSymbol;
import com.mammb.code.piecetable.Pos;
import com.mammb.code.piecetable.TextEdit;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The text edit content.
 * @author Naotsugu Kobayashi
 */
public class TextEditContent implements Content {

    /** The text edit. */
    private final TextEdit edit;
    /** The flushes(text being edited with IME). */
    private final List<Point.PointText> flushes = new ArrayList<>();
    /** Whether it has been modified or not. */
    private boolean modified;

    /**
     * Constructor.
     */
    public TextEditContent() {
        this.edit = TextEdit.of();
    }

    /**
     * Constructor.
     * @param path the path of content
     */
    public TextEditContent(Path path) {
        this.edit = TextEdit.of(path);
    }

    /**
     * Constructor.
     * @param path the path of content
     * @param traverseCallback the traverseCallback
     */
    public TextEditContent(Path path, Function<byte[], Boolean> traverseCallback) {
        this.edit = TextEdit.of(path, traverseCallback::apply);
    }

    @Override
    public Point insert(Point point, String text) {
        String txt = edit.rowEnding().unify(text).toString();
        modified = true;
        var pos = edit.insert(point.row(), point.col(), txt);
        return Point.of(pos.row(), pos.col());
    }

    @Override
    public List<Point> insert(List<Point> points, String text) {
        String txt = edit.rowEnding().unify(text).toString();
        modified = true;
        var pos = edit.insert(points.stream()
            .map(p -> Pos.of(p.row(), p.col())).toList(), txt);
        return pos.stream().map(p -> Point.of(p.row(), p.col())).toList();
    }

    @Override
    public String delete(Point point) {
        modified = true;
        return edit.delete(point.row(), point.col());
    }

    @Override
    public List<Point> delete(List<Point> points) {
        modified = true;
        var pos = edit.delete(points.stream()
            .map(p -> Pos.of(p.row(), p.col())).toList());
        return pos.stream().map(p -> Point.of(p.row(), p.col())).toList();
    }

    @Override
    public Point backspace(Point point) {
        modified = true;
        var pos = edit.backspace(point.row(), point.col());
        return Point.of(pos.row(), pos.col());
    }

    @Override
    public List<Point> backspace(List<Point> points) {
        modified = true;
        var pos = edit.backspace(points.stream()
            .map(p -> Pos.of(p.row(), p.col())).toList());
        return pos.stream().map(p -> Point.of(p.row(), p.col())).toList();
    }

    @Override
    public Point replace(Point start, Point end, String text) {
        String txt = edit.rowEnding().unify(text).toString();
        var pos = edit.replace(start.row(), start.col(), end.row(), end.col(), _ -> txt);
        return Point.of(pos.row(), pos.col());
    }

    @Override
    public List<Point.Range> replace(List<Point.Range> ranges, Function<String, String> fun) {

        modified = true;

        var requests = ranges.stream().map(r -> new TextEdit.Replace(
            Pos.of(r.start().row(), r.start().col()),
            Pos.of(r.end().row(), r.end().col()),
            fun::apply)).toList();

        return edit.replace(requests).stream()
            .map(r -> new Point.Range(
                Point.of(r.from().row(), r.from().col()),
                Point.of(r.to().row(), r.to().col())))
            .toList();
    }

    @Override
    public List<Point> undo() {
        var ret = edit.undo().stream().map(p -> Point.of(p.row(), p.col())).toList();
        modified |= !ret.isEmpty();
        return ret;
    }

    @Override
    public List<Point> redo() {
        var ret = edit.redo().stream().map(p -> Point.of(p.row(), p.col())).toList();
        modified |= !ret.isEmpty();
        return ret;
    }

    @Override
    public String getText(int row) {
        if (!flushes.isEmpty()) {
            var sb = new StringBuilder(edit.getText(row));
            flushes.stream().filter(p -> p.point().row() == row)
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
        modified = false;
    }

    @Override
    public Point insertFlush(Point point, String text) {
        flushes.add(new Point.PointText(point, text));
        return Point.of(point.row(), point.col() + text.length());
    }

    @Override
    public void clearFlush() {
        flushes.clear();
    }

    @Override
    public List<Point> findAll(String text) {
        var founds = edit.findAll(text);
        return founds.stream()
            .map(found -> Point.of(found.row(), found.col()))
            .toList();
    }

    @Override
    public Optional<Point> findNext(Point base, String text) {
        // TODO
        return Optional.empty();
    }

    @Override
    public Optional<Point> findPrev(Point base, String text) {
        // TODO
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case RowEndingSymbol _ -> (R) edit.rowEnding().toString();
            case CharsetSymbol _   -> (R) edit.charset().displayName();
            case Modified _        -> (R) (Boolean) isModified();
            case Bom _             -> (R) edit.bom();
            case ContentPath _     -> (R) edit.path();
            default                -> null;
        };
    }

    /**
     * Whether it has been modified or not.
     * @return {@code true} if it has been modified
     */
    private boolean isModified() {
        if (flushes.isEmpty() && !edit.hasUndoRecord()) {
            return false;
        }
        return modified;
    }

}
