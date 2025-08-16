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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Pair;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.text.SubText;
import com.mammb.code.editor.core.text.Text;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

/**
 * The BracketFind.
 * @author Naotsugu Kobayashi
 */
public class BracketFind {

    /**
     * Applies bracket matching logic to find points in a text based on the given caret point
     * and the characters surrounding it. Depending on the type of bracket and its position,
     * this method searches forwards or backwards across the lines of text to identify matching brackets.
     * @param point the current caret point in the text
     * @param charAtCaret a pair representing the characters at and next to the caret position
     * @param lines the list of text lines to search within
     * @return a list of points that indicate matching bracket positions, or an empty list if no matches are found
     */
    static List<Point> apply(Point point, Pair<String> charAtCaret, List<Text> lines) {

        if (lines.isEmpty()) {
            return List.of();
        }

        var left = bracket(charAtCaret.left());
        var right = bracket(charAtCaret.right());

        if (left.isEmpty() && right.isEmpty()) {
            return List.of();
        }

        List<Text> rows = rows(lines);

        if (left.isPresent() && left.get().isClose()) {
            var c = Point.of(point.row(), point.col() - 1);
            return findBackward(rows, c, left.get()).map(p -> List.of(p, c)).orElse(List.of(c));
        } else if (right.isPresent() && right.get().isOpen()) {
            var base = Point.of(point.row(), point.col() + 1);
            return findForward(rows, base, right.get()).map(p -> List.of(point, p)).orElse(List.of(point));
        } else if (left.isPresent() && left.get().isOpen()) {
            var c = Point.of(point.row(), point.col() - 1);
            return findForward(rows, point, left.get()).map(p -> List.of(c, p)).orElse(List.of(c));
        } else if (right.isPresent() && right.get().isClose()) {
            return findBackward(rows, point, right.get()).map(p -> List.of(p, point)).orElse(List.of(point));
        }

        return List.of();
    }

    private static Optional<Point> findForward(List<Text> rows, Point base, Bracket bracket) {
        assert bracket.isOpen();
        int nest = 0;
        for (Text row : rows) {
            if (row.row() < base.row()) continue;
            int col = (row.row() == base.row()) ? base.col() : 0;
            for (; col < row.length(); col++) {
                char ci = row.value().charAt(col);
                if (ci == bracket.type().close()) {
                    if (nest == 0) {
                        return Optional.of(Point.of(row.row(), col));
                    }
                    nest--;
                } else if (ci == bracket.type().open()) {
                    nest++;
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<Point> findBackward(List<Text> rows, Point base, Bracket bracket) {
        assert bracket.isClose();
        int nest = 0;
        for (int i = rows.size() - 1; i >= 0; i--) {
            Text row = rows.get(i);
            if (row.row() > base.row()) continue;
            int col = (row.row() == base.row()) ? base.col() - 1 : row.length() - 1;
            for (; col >= 0; col--) {
                char ci = row.value().charAt(col);
                if (ci == bracket.type().close()) {
                    nest++;
                } else if (ci == bracket.type().open()) {
                    if (nest == 0) {
                        return Optional.of(Point.of(row.row(), col));
                    }
                    nest--;
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<Bracket> bracket(String string) {
        if (string == null || string.isBlank()) return Optional.empty();
        return Bracket.valueOf(string.charAt(0));
    }

    private static List<Text> rows(List<Text> lines) {
        List<Text> texts = new ArrayList<>();
        for (Text text : lines) {
            Text row = (text instanceof SubText sub) ? sub.parent() : text;
            if (texts.isEmpty()) {
                texts.add(row);
            } else if (texts.getLast().row() != row.row()) {
                texts.add(row);
            }
        }
        return texts;
    }

}
