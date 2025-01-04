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
package com.mammb.code.editor.core.syntax;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * The block scopes.
 * @author Naotsugu Kobayashi
 */
public class BlockScopes {

    private final TreeMap<Anchor, Token> scopes = new TreeMap<>();

    public void clear(int row) {
        scopes.subMap(new Anchor(row, 0), new Anchor(row, Integer.MAX_VALUE)).clear();
    }

    public void putOpen(int row, int col, Range type) {
        scopes.put(new Anchor(row, col), new StartToken(type));
    }

    public void putClose(int row, int col, Range type) {
        scopes.put(new Anchor(row, col), new EndToken(type));
    }

    public void putNeutral(int row, int col, Neutral type) {
        scopes.put(new Anchor(row, col), new OpenToken(type));
    }

    public Optional<BlockType> inScope(int row, int col) {
        Deque<Token> neutralStack = new ArrayDeque<>();
        Deque<Token> rangeStack = new ArrayDeque<>();
        for (var e : scopes.headMap(new Anchor(row, col)).entrySet()) {
            switch (e.getValue()) {
                case StartToken t -> rangeStack.push(t);
                case EndToken t -> {
                    if (rangeStack.peek() instanceof StartToken startToken &&
                            Objects.equals(t.type(), startToken.type())) {
                        rangeStack.pop();
                    }
                }
                case OpenToken t  -> {
                    if (!neutralStack.isEmpty() &&
                            Objects.equals(t.type(), neutralStack.peek().type())) {
                        neutralStack.pop();
                    } else {
                        neutralStack.push(t);
                    }
                }
            }
        }
        if (!rangeStack.isEmpty()) {
            return Optional.of(rangeStack.peek().type());
        }
        if (!neutralStack.isEmpty()) {
            return Optional.of(neutralStack.peek().type());
        }
        return Optional.empty();
    }

    record Anchor(int row, int col) implements Comparable<Anchor> {
        @Override
        public int compareTo(Anchor that) {
            int c = Integer.compare(this.row, that.row);
            return c == 0 ? Integer.compare(this.col, that.col) : c;
        }
    }

    private sealed interface Token { BlockType type(); }
    private record OpenToken(Neutral type) implements Token { }
    private record StartToken(Range type) implements Token { }
    private record EndToken(Range type) implements Token { }


    public interface BlockType {
        String open();
        String close();
        static Neutral neutral(String open) {
            record NeutralRecord(String open) implements Neutral { }
            return new NeutralRecord(open);
        }
        static NeutralAttributed neutral(String open, Syntax syntax) {
            return new NeutralRecord(open, syntax);
        }
        static Range range(String open, String close) {
            record RangeRecord(String open, String close) implements Range { }
            return new RangeRecord(open, close);
        }
    }
    public interface Neutral extends BlockType {
        default String close() { return open(); }
    }
    public interface NeutralAttributed extends Neutral {
        Object attribute();
    }
    public interface Range extends BlockType { }

    private static class NeutralRecord implements NeutralAttributed {
        private final String open;
        private final Object attribute;
        public NeutralRecord(String open, Syntax syntax) {
            this(open, (Object) syntax);
        }
        private NeutralRecord(String open, Object attribute) {
            this.open = open;
            this.attribute = attribute;
        }
        @Override public String open() { return open; }
        @Override public Object attribute() { return attribute; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NeutralRecord that = (NeutralRecord) o;
            return Objects.equals(open, that.open);
        }
        @Override public int hashCode() {
            return Objects.hashCode(open);
        }
    }

}
