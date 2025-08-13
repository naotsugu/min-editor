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

    private void unify(Consumer<Line<T>> consumer) {

        int i = 0;
        int j = 0;
        while (i < source.org().size() || j < source.rev().size()) {
            if (changes.isEmpty()) {
                consumer.accept(new Line<>(i, j, source.rev().get(j++)));
                i++;
                continue;
            }
            Change c = changes.getFirst();
            if (c.type == Change.Type.CHANGE && (c.orgFrom <= i && i < c.orgTo || c.revFrom <= j && j < c.revTo)) {
                if (c.orgTo - 1 == i && c.revTo - 1 == j) changes.removeFirst();
                if (source.org().size() > i) {
                    consumer.accept(new Line<>(i, -1, source.org().get(i++)));
                }
                if (source.rev().size() > j) {
                    consumer.accept(new Line<>(-1, j, source.rev().get(j++)));
                }
            } else if (c.type == Change.Type.INSERT && c.revFrom <= j && j < c.revTo) {
                if (c.revTo - 1 == j) changes.removeFirst();
                consumer.accept(new Line<>(-1, j, source.rev().get(j++)));
            } else if (c.type == Change.Type.DELETE && c.orgFrom <= i && i < c.orgTo) {
                if (c.orgTo - 1 == i) changes.removeFirst();
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
