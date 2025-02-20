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
package com.mammb.code.editor.core.syntax;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import com.mammb.code.editor.core.syntax.BlockToken.Close;
import com.mammb.code.editor.core.syntax.BlockToken.Open;

/**
 * The scope stack.
 * @author Naotsugu Kobayashi
 */
public class ScopeStack {

    private final TreeMap<Anchor, BlockToken> scopes = new TreeMap<>();
    private final Deque<BlockToken> stack = new ArrayDeque<>();

    public ScopeStack() {
    }

    void clear(int row) {
        var map = scopes.tailMap(Anchor.min(row));
        if (map.isEmpty()) {
            return;
        }
        map.clear();
        fillStack();
    }

    void put(int row, int col, BlockToken token) {
        scopes.put(new Anchor(row, col), token);
        stack.push(token);
    }

    Optional<Open> current() {
        return Optional.ofNullable(stack.peek())
            .filter(Open.class::isInstance)
            .map(Open.class::cast);
    }


    private void fillStack() {

        stack.clear();

        for (BlockToken token : scopes.values()) {

            if (token instanceof Open open) {
                stack.push(open);

            } else if (token instanceof Close close) {
                BlockToken latest = stack.peek();
                if (latest instanceof Open open &&
                    Objects.equals(token.type(), latest.type())) {
                    stack.pop();
                }
            }
        }
    }

    record Anchor(int row, int col) implements Comparable<Anchor> {
        static Anchor min(int row) { return new Anchor(row, 0); }
        static Anchor max(int row) { return new Anchor(row, Integer.MAX_VALUE); }
        @Override
        public int compareTo(Anchor that) {
            int c = Integer.compare(this.row, that.row);
            return c == 0 ? Integer.compare(this.col, that.col) : c;
        }
    }

}
