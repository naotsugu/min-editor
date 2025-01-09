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
package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.syntax.LexerSource;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import com.mammb.code.editor.core.syntax2.TokenType.*;

/**
 * The block scopes.
 * @author Naotsugu Kobayashi
 */
public class BlockScopes {

    private final TreeMap<Anchor, Token> scopes = new TreeMap<>();

    private final List<Block> blocks = new ArrayList<>();


    public BlockScopes(Block... blocks) {
        this.blocks.addAll(List.of(blocks));
    }

    void put(int row, Supplier<String> texts) {

        clear(row);

        for (int i = row; ; i++) {

            String text = texts.get();
            if (text == null) {
                break;
            }

            var source = LexerSource.of(i, text);
            while (source.hasNext()) {
                var peek = source.peek();
                Optional<Token> token = inScope(source.row(), peek.index());

                if (token.isPresent()) {

                } else {

                }

            }
        }
    }

    void clear(int row) {
        scopes.subMap(new Anchor(row, 0), new Anchor(row, Integer.MAX_VALUE)).clear();
    }

    Optional<Token> inScope(int row, int col) {

        Deque<Token> neutralStack = new ArrayDeque<>();
        Deque<Token> rangeStack = new ArrayDeque<>();

        for (var e : scopes.headMap(new Anchor(row, col)).entrySet()) {

            if (e.getValue().isRangeBlockStart()) {
                rangeStack.push(e.getValue());
            } else if (e.getValue().isRangeBlockEnd()) {
                Token token = rangeStack.peek();
                if (token != null && Objects.equals(token.type(), e.getValue().type())) {
                    rangeStack.pop();
                }
            } else {
                Token token = rangeStack.peek();
                if (token != null && Objects.equals(token.type(), e.getValue().type())) {
                    neutralStack.pop();
                } else {
                    neutralStack.push(e.getValue());
                }
            }
        }
        if (!rangeStack.isEmpty()) {
            return Optional.of(rangeStack.peek());
        }
        if (!neutralStack.isEmpty()) {
            return Optional.of(neutralStack.peek());
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

}
