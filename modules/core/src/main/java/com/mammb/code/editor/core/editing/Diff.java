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
package com.mammb.code.editor.core.editing;

import com.mammb.code.editor.core.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The diff.
 * @author Naotsugu Kobayashi
 */
public class Diff {

    public static List<CharSequence> fullLines(final Source<CharSequence> org, final Source<CharSequence> rev) {
        List<Change> changes = buildChanges(buildPath(org, rev));
        changes.forEach(System.out::println);
        return text(changes, org, rev);
    }

    private static <T> Node buildPath(final Source<T> org, final Source<T> rev) {

        final int n = org.size();
        final int m = rev.size();

        final int max = n + m + 1;
        final int size = 1 + 2 * max;
        final int mid = size / 2;
        final Node[] diagonal = new Node[size];
        diagonal[mid + 1] = Node.of();

        for (int d = 0; d < max; d++) {
            for (int k = -d; k <= d; k += 2) {
                final int mk = mid + k;
                final Node prev;
                int i;

                if ((k == -d) || (k != d && diagonal[mk - 1].i < diagonal[mk + 1].i)) {
                    i = diagonal[mk + 1].i;
                    prev = diagonal[mk + 1];
                } else {
                    i = diagonal[mk - 1].i + 1;
                    prev = diagonal[mk - 1];
                }
                diagonal[mk - 1] = null; // no longer used

                int j = i - k;

                Node node = Node.stepOf(i, j, prev);

                while (i < n && j < m && Objects.equals(org.get(i), rev.get(j))) {
                    i++;
                    j++;
                }

                if (i != node.i) {
                    node = Node.snakeOf(i, j, node);
                }

                diagonal[mk] = node;

                if (i >= n && j >= m) {
                    return diagonal[mk];
                }
            }
            diagonal[mid + d - 1] = null;
        }
        throw new IllegalStateException("could not find a diff path");
    }

    private static List<Change> buildChanges(Node path) {

        List<Change> changes = new ArrayList<>();
        if (path.snake) {
            path = path.prev;
        }

        while (path != null && path.prev != null && path.prev.j >= 0) {

            if (path.snake) {
                throw new IllegalStateException("illegal path");
            }

            int i = path.i;
            int j = path.j;

            path = path.prev;

            Change.Type type = (path.i == i && path.j != j)
                ? Change.Type.INSERT
                : (path.i != i && path.j == j)
                    ? Change.Type.DELETE
                    : Change.Type.CHANGE;

            changes.addFirst(new Change(type, path.i, i, path.j, j));

            if (path.snake) {
                path = path.prev;
            }
        }
        return changes;
    }

    private static List<CharSequence> text(final List<Change> changes,
            final Source<CharSequence> org, final Source<CharSequence> rev) {

        final String FMT_D = " %04d       : -  %s" + "\n";
        final String FMT_I = "       %04d : +  %s" + "\n";
        final String FMT_N = " %04d  %04d :    %s" + "\n";

        List<CharSequence> ret = new ArrayList<>();

        int i = 0;
        int j = 0;
        while (i < org.size() || j < rev.size()) {
            if (changes.isEmpty()) {
                ret.add(String.format(FMT_N, i, j, rev.get(j++)));
                i++;
                continue;
            }
            Change c = changes.getFirst();
            if (c.type == Change.Type.CHANGE && (c.orgFrom <= i && i < c.orgTo || c.revFrom <= j && j < c.revTo)) {
                if (c.orgTo - 1 == i && c.revTo - 1 == j) changes.removeFirst();
                if (org.size() > i) {
                    ret.add(String.format(FMT_D, i, org.get(i++)));
                }
                if (rev.size() > j) {
                    ret.add(String.format(FMT_I, j, rev.get(j++)));
                }
            } else if (c.type == Change.Type.INSERT && c.revFrom <= j && j < c.revTo) {
                if (c.revTo - 1 == j) changes.removeFirst();
                ret.add(String.format(FMT_I, j, rev.get(j++)));
            } else if (c.type == Change.Type.DELETE && c.orgFrom <= i && i < c.orgTo) {
                if (c.orgTo - 1 == i) changes.removeFirst();
                ret.add(String.format(FMT_D, i, org.get(i++)));
            } else {
                ret.add(String.format(FMT_N, i, j, rev.get(j++)));
                i++;
            }
        }
        return ret;
    }

    public interface Source<T> {
        T get(int index);
        int size();
        static <T> Source<T> of(List<T> list) {
            return new Source<T>() {
                @Override public T get(int index) { return list.get(index); }
                @Override public int size() { return list.size(); }
            };
        }
        static Source<CharSequence> of(Path path, Charset cs) {
            return new Source<>() {
                final List<String> list = Files.readAllLines(path, cs);
                @Override public CharSequence get(int index) { return list.get(index); }
                @Override public int size() { return list.size(); }
            };
        }
    }

    private record Node(int i, int j, boolean snake, Node prev) {
        static Node of() { return new Node(0, -1, true, null); }
        static Node snakeOf(int i, int j, Node prev) { return new Node(i, j, true, prev); }
        static Node stepOf(int i, int j, Node prev) {
            return new Node(i, j, false, prev == null ? null : prev.prevSnake());
        }
        private Node prevSnake() {
            return isBootstrap() ? null
                : (!snake && prev != null)
                ? prev.prevSnake()
                : this;
        }
        private boolean isBootstrap() { return (i < 0 || j < 0); }
    }

    private record Change(Type type, int orgFrom, int orgTo, int revFrom, int revTo) {
        enum Type { CHANGE, DELETE, INSERT }
    }

}
