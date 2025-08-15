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

import com.mammb.code.editor.core.diff.ChangeSet.Change;
import java.util.ArrayList;
import java.util.List;

/**
 * The diff.
 * This class implements the diff algorithm based on
 * "An O(ND) Difference Algorithm and Its Variations" by Eugene W. Myers.
 * @author Naotsugu Kobayashi
 */
public final class Diff {

    /**
     * Run the diff algorithm.
     * @param source the source pair
     * @return the change set
     * @param <T> the type of source element
     */
    public static <T> ChangeSet<T> run(SourcePair<T> source) {
        Node path = buildPath(source);
        List<Change> changes = buildChanges(path);
        return new ChangeSet<>(source, changes);
    }


    /**
     * Build the path of the shortest edit script.
     * @param source the source pair
     * @return the end node of the path
     * @param <T> the type of source element
     */
    private static <T> Node buildPath(final SourcePair<T> source) {

        final int n = source.org().size();
        final int m = source.rev().size();

        // The maximum number of differences is n + m
        final int max = n + m + 1;
        // The size of the diagonal array. +1 for the center, +1 for the start offset
        final int size = 1 + 2 * max;
        final int mid = size / 2;

        final Node[] diagonal = new Node[size];
        diagonal[mid + 1] = Node.of();

        // d is the number of differences
        for (int d = 0; d < max; d++) {
            // k is the diagonal index. k = i - j
            for (int k = -d; k <= d; k += 2) {

                final int mk = mid + k;
                final Node prev;
                int i;

                if ((k == -d) || (k != d && diagonal[mk - 1].i < diagonal[mk + 1].i)) {
                    // move down, meaning an insertion
                    i = diagonal[mk + 1].i;
                    prev = diagonal[mk + 1];
                } else {
                    // move right, meaning a deletion
                    i = diagonal[mk - 1].i + 1;
                    prev = diagonal[mk - 1];
                }
                // De-reference to allow for garbage collection
                diagonal[mk - 1] = null;

                int j = i - k;

                Node node = Node.stepOf(i, j, prev);

                // Follow the snake (common sequence)
                while (i < n && j < m && source.equalsAt(i, j)) {
                    i++;
                    j++;
                }

                if (i != node.i) {
                    node = Node.snakeOf(i, j, node);
                }

                diagonal[mk] = node;

                if (i >= n && j >= m) {
                    // found the end of the path
                    return diagonal[mk];
                }
            }
            // De-reference to allow for garbage collection
            diagonal[mid + d - 1] = null;
        }
        throw new IllegalStateException("could not find a diff path");
    }


    /**
     * Build the list of changes from the path.
     * @param path the end node of the path
     * @return the list of changes
     */
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

            Change.Type type = getChangeType(i, j, path);
            changes.addFirst(new Change(type, path.i, i, path.j, j));

            if (path.snake) {
                path = path.prev;
            }
        }
        return changes;
    }


    /**
     * Get the type of change.
     * @param i the current i
     * @param j the current j
     * @param path the previous node
     * @return the type of change
     */
    private static Change.Type getChangeType(int i, int j, Node path) {
        if (path.i == i) {
            return Change.Type.INSERT;
        } else if (path.j == j) {
            return Change.Type.DELETE;
        } else {
            return Change.Type.CHANGE;
        }
    }


    /**
     * Represents a node in the diff path.
     * @param i the line number in the original source
     * @param j the line number in the revised source
     * @param snake true if this node is part of a snake (common sequence)
     * @param prev the previous node in the path
     */
    private record Node(int i, int j, boolean snake, Node prev) {

        static Node of() {
            return new Node(0, -1, true, null);
        }

        static Node snakeOf(int i, int j, Node prev) {
            return new Node(i, j, true, prev);
        }

        static Node stepOf(int i, int j, Node prev) {
            return new Node(i, j, false, prev == null ? null : prev.prevSnake());
        }

        private Node prevSnake() {
            return isBootstrap()
                ? null
                : (!snake && prev != null)
                    ? prev.prevSnake()
                    : this;
        }

        private boolean isBootstrap() {
            return (i < 0 || j < 0);
        }
    }

}
