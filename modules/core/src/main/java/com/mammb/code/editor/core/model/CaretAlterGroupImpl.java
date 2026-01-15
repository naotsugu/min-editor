/*
 * Copyright 2023-2026 the original author or authors.
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

import com.mammb.code.editor.core.Caret;
import com.mammb.code.editor.core.CaretAlterGroup;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The CaretAlterGroup.
 * @author Naotsugu Kobayashi
 */
public class CaretAlterGroupImpl implements CaretAlterGroup {

    private List<DirectedStack> stacks;

    private List<Caret> peer;

    public CaretAlterGroupImpl(List<Caret> carets) {
        peer = carets;
        stacks = carets.stream().map(DirectedStack::of).collect(Collectors.toList());
    }

    public void down(Function<Caret, Caret> nextAt) {
        for (DirectedStack stack : stacks) {
            var result = stack.down(nextAt);
            if (result.caret == null) continue;
            if (result.grow) {
                peer.add(result.caret);
            } else {
                peer.remove(result.caret);
            }
        }
    }

    public void up(Function<Caret, Caret> nextAt) {
        for (DirectedStack stack : stacks) {
            var result = stack.up(nextAt);
            if (result.caret == null) continue;
            if (result.grow) {
                peer.add(result.caret);
            } else {
                peer.remove(result.caret);
            }
        }
    }

    void add(Caret caret) {
        stacks.add(DirectedStack.of(caret));
    }

    static class DirectedStack {

        private Deque<Caret> stack;
        private Direction direction;

        private DirectedStack(Deque<Caret> stack, Direction direction) {
            this.stack = stack;
            this.direction = direction;
        }

        public static DirectedStack of(Caret caret) {
            return new DirectedStack(new ArrayDeque<>(List.of(caret)), Direction.NEUTRAL);
        }

        public Result down(Function<Caret, Caret> nextAt) {
            return switch (direction) {
                case NEUTRAL, DOWN -> push(nextAt);
                case UP -> pop();
            };
        }

        public Result up(Function<Caret, Caret> nextAt) {
            return switch (direction) {
                case NEUTRAL, UP -> push(nextAt);
                case DOWN -> pop();
            };
        }

        private Result push(Function<Caret, Caret> nextAt) {
            var at = nextAt.apply(stack.peek());
            if (at == null) return new Result(null, false);
            var caret = Caret.of(at.row(), at.col());
            stack.push(caret);
            return new Result(caret, true);
        }

        private Result pop() {
            var caret = stack.pop();
            if (stack.size() == 1) {
                direction = Direction.NEUTRAL;
            }
            if (stack.isEmpty()) throw new IllegalStateException();
            return new Result(caret, false);
        }

        enum Direction { UP, DOWN, NEUTRAL }

    }

    record Result(Caret caret, boolean grow) { }

}
