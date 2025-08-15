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
package com.mammb.code.editor.core.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The change set.
 * @author Naotsugu Kobayashi
 */
public record ChangeSet<T>(SourcePair<T> source, List<Change> changes) {

    public record Change(Change.Type type, int orgFrom, int orgTo, int revFrom, int revTo) {
        enum Type { CHANGE, DELETE, INSERT }
    }

    public List<String> unifyTexts() {
        List<String> list = new ArrayList<>();
        unify(line -> {
            if (line.both()) {
                list.add("  " + line.text);
            } else if (line.left()) {
                list.add("- " + line.text);
            } else if (line.right()) {
                list.add("+ " + line.text);
            }
        });
        return list;
    }

    public List<String> unifyTextsWithNumbers() {

        int n = Math.max(4, String.valueOf(source.sizeMax()).length());

        String SP = " ";
        String NUM = "%0" + n + "d";
        String TAB = " ".repeat(n);
        String FMT_D = SP + NUM + SP.repeat(2) + TAB + " : -  %s";
        String FMT_I = SP + TAB + SP.repeat(2) + NUM + " : +  %s";
        String FMT_N = SP + NUM + SP.repeat(2) + NUM + " :    %s";

        List<String> list = new ArrayList<>();
        unify(line -> {
            if (line.both()) {
                list.add(FMT_N.formatted(line.i + 1, line.j + 1, line.text));
            } else if (line.left()) {
                list.add(FMT_D.formatted(line.i + 1, line.text));
            } else if (line.right()) {
                list.add(FMT_I.formatted(line.j + 1, line.text));
            }
        });
        return list;
    }

    public String unifiedFormText(int contextSize) {

        if (changes.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (source.named()) {
            sb.append("--- ").append(source.org().name()).append(System.lineSeparator());
            sb.append("+++ ").append(source.rev().name()).append(System.lineSeparator());
        }

        List<Line<T>> allLines = new ArrayList<>();
        unify(allLines::add);

        boolean[] isChange = new boolean[allLines.size()];
        for (int i = 0; i < allLines.size(); i++) {
            if (!allLines.get(i).both()) {
                isChange[i] = true;
            }
        }

        boolean[] inHunk = new boolean[allLines.size()];
        for (int i = 0; i < allLines.size(); i++) {
            if (isChange[i]) {
                for (int j = Math.max(0, i - contextSize); j <= Math.min(allLines.size() - 1, i + contextSize); j++) {
                    inHunk[j] = true;
                }
            }
        }

        for (int i = 0; i < allLines.size(); i++) {
            if (inHunk[i]) {
                int hunkStart = i;
                int hunkEnd = i;
                while (hunkEnd + 1 < allLines.size() && inHunk[hunkEnd + 1]) {
                    hunkEnd++;
                }

                int orgStartLine = -1, revStartLine = -1;
                int orgLineCount = 0, revLineCount = 0;

                for (int k = hunkStart; k <= hunkEnd; k++) {
                    Line<T> line = allLines.get(k);
                    if (line.left()) {
                        if (orgStartLine == -1) orgStartLine = line.i + 1;
                        orgLineCount++;
                    }
                    if (line.right()) {
                        if (revStartLine == -1) revStartLine = line.j + 1;
                        revLineCount++;
                    }
                }

                sb.append("@@ -").append(orgStartLine).append(',').append(orgLineCount)
                    .append(" +").append(revStartLine).append(',').append(revLineCount)
                    .append(" @@").append(System.lineSeparator());

                for (int k = hunkStart; k <= hunkEnd; k++) {
                    Line<T> line = allLines.get(k);
                    if (line.both()) {
                        sb.append("  ").append(line.text).append(System.lineSeparator());
                    } else if (line.left()) {
                        sb.append("- ").append(line.text).append(System.lineSeparator());
                    } else if (line.right()) {
                        sb.append("+ ").append(line.text).append(System.lineSeparator());
                    }
                }
                i = hunkEnd;
            }
        }
        return sb.toString();
    }

    private void unify(Consumer<Line<T>> consumer) {

        int i = 0;
        int j = 0;
        List<Change> stack = new ArrayList<>(this.changes);

        while (i < source.org().size() || j < source.rev().size()) {
            if (stack.isEmpty()) {
                if (i < source.org().size() && j < source.rev().size()) {
                    consumer.accept(new Line<>(i, j, source.rev().get(j++)));
                    i++;
                } else if (i < source.org().size()) {
                    consumer.accept(new Line<>(i, -1, source.org().get(i++)));
                } else {
                    consumer.accept(new Line<>(-1, j, source.rev().get(j++)));
                }
                continue;
            }
            Change c = stack.getFirst();
            if (c.type == Change.Type.CHANGE && (c.orgFrom <= i && i < c.orgTo || c.revFrom <= j && j < c.revTo)) {
                if (c.orgTo - 1 == i && c.revTo - 1 == j) stack.removeFirst();
                if (source.org().size() > i) {
                    consumer.accept(new Line<>(i, -1, source.org().get(i++)));
                }
                if (source.rev().size() > j) {
                    consumer.accept(new Line<>(-1, j, source.rev().get(j++)));
                }
            } else if (c.type == Change.Type.INSERT && c.revFrom <= j && j < c.revTo) {
                if (c.revTo - 1 == j) stack.removeFirst();
                consumer.accept(new Line<>(-1, j, source.rev().get(j++)));
            } else if (c.type == Change.Type.DELETE && c.orgFrom <= i && i < c.orgTo) {
                if (c.orgTo - 1 == i) stack.removeFirst();
                consumer.accept(new Line<>(i, -1, source.org().get(i++)));
            } else {
                consumer.accept(new Line<>(i, j, source.rev().get(j++)));
                i++;
            }
        }
    }

    private record Line<T> (int i, int j, T text) {
        boolean left() { return i >= 0; }
        boolean right() { return j >= 0; }
        boolean both() { return i >= 0 && j >= 0; }
    }

}
