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

    Optional<Point> pair(Point point, Pair<String> charAtCaret, List<Text> lines) {

        if (lines.isEmpty()) {
            return Optional.empty();
        }

        var left = bracket(charAtCaret.left());
        var right = bracket(charAtCaret.right());

        if (left.isEmpty() && right.isEmpty()) {
            return Optional.empty();
        }

        List<Text> rows = rows(lines);

        if (left.isPresent() && left.get().isClose()) {
            return findBackward(rows, point, left.get());
        } else if (right.isPresent() && right.get().isOpen()) {
            return findForward(rows, point, right.get());
        } else if (left.isPresent() && left.get().isOpen()) {
            return return findForward(rows, point, left.get());
        } else if (right.isPresent() && right.get().isClose()) {
            return return findBackward(rows, point, right.get());
        }

        return Optional.empty();
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
        // TODO
        return Optional.empty();
    }

    private Optional<Bracket> bracket(String string) {
        if (string == null || string.isBlank()) return Optional.empty();
        return Bracket.valueOf(string.charAt(0));
    }

    private static List<Text> rows(List<Text> lines) {
        List<Text> texts = new ArrayList<>();
        for (Text text : lines) {
            Text row = (lines.getFirst() instanceof SubText sub) ? sub.parent() : text;
            if (texts.isEmpty()) {
                texts.add(row);
            } else if (texts.getLast().row() != row.row()) {
                texts.add(row);
            }
        }
        return texts;
    }

//    private static Optional<Point> findBackward(List<Text> lines, int lineIndex, int col, Bracket bracket) {
//        int nest = 1;
//        for (int i = lineIndex; i >= 0; i--) {
//            Text text = lines.get(i);
//            for (int j = (i == lineIndex) ? col - 1 : text.length() - 1; j >= 0; j--) {
//                char ch = text.value().charAt(j);
//                if (ch == bracket.type().close()) {
//                    nest++;
//                } else if (ch == bracket.type().open()) {
//                    nest--;
//                }
//                if (nest == 0) {
//                    int offset = (text instanceof SubText sub) ? sub.fromIndex() : 0;
//                    return Optional.of(Point.of(text.row(), offset + j));
//                }
//            }
//        }
//        return Optional.empty();
//    }
}
