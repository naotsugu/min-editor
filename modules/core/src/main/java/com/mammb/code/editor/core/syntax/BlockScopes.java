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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The block scopes.
 * @author Naotsugu Kobayashi
 */
public class BlockScopes {

    /** The scope stacks. */
    private final ScopeStack scopes = new ScopeStack();
    /** The block types. */
    private final List<BlockType> types = new ArrayList<>();

    /**
     * Constructor.
     * @param blockTypes
     */
    public BlockScopes(BlockType... blockTypes) {
        types.addAll(Stream.of(blockTypes).filter(Objects::nonNull).toList());
    }

    /**
     * Get whether this block types is empty or not.
     * @return {@code true}, if this block types is empty
     */
    public boolean isEmpty() {
        return types.isEmpty();
    }

    /**
     * Put the source texts.
     * @param sources the source texts
     */
    public void put(Iterator<LexerSource> sources) {

        if (!sources.hasNext()) return;

        LexerSource source = sources.next();
        scopes.clear(source.row());

        for (;;) {

            var maybeBlockOpen = scopes.current();
            if (maybeBlockOpen.isPresent()) {
                readUntilClose(maybeBlockOpen.get().type(), source);
            } else {
                readUntilOpen(source);
            }

            if (!source.hasNext()) {
                if (!sources.hasNext()) break;
                source = sources.next();
            }
        }
    }

    /**
     * Read the lexer source.
     * @param source the lexer source
     * @return the block span
     */
    public Optional<BlockSpan> read(LexerSource source) {

        if (!source.hasNext()) {
            return Optional.empty();
        }

        int index = source.index();
        if (index == 0) {
            scopes.clear(source.row());
        }

        var maybeBlockOpen = scopes.current();
        if (maybeBlockOpen.isEmpty() && readOpen(source).isPresent()) {
            maybeBlockOpen = scopes.current();
        }

        if (maybeBlockOpen.isPresent()) {
            BlockToken token = maybeBlockOpen.get();
            readUntilClose(token.type(), source);
            return Optional.of(new BlockSpan(token, index, source.index() - index));
        }

        source.rollbackPeek();
        return Optional.empty();
    }


    public record BlockSpan(BlockToken token, int index, int length) {
        public BlockType type() { return token.type(); }
        public <T> T with() {
            if (token instanceof BlockToken.BlockTokenWith<?> a) {
                @SuppressWarnings("unchecked")
                T ret = (T) a.with();
                return ret;
            }
            return null;
        }
    }

    private void readUntilClose(BlockType type, LexerSource source) {
        while (source.hasNext()) {
            var peek = source.peek();
            if (peek.ch() == type.close().charAt(0) && source.match(type.close())) {
                source.next(type.close().length());
                int col = source.index();
                scopes.put(source.row(), col, BlockToken.close(type));
                return;
            }
            source.commitPeek();
        }
    }

    private void readUntilOpen(LexerSource source) {
        while (source.hasNext()) {
            readOpen(source);
            source.commitPeek();
        }
    }

    private Optional<BlockToken> readOpen(LexerSource source) {
        var peek = source.peek();

        Optional<BlockType> blockType = matches(peek.ch()).stream()
            .filter(type -> source.match(type.open()))
            .findFirst();

        if (blockType.isPresent()) {
            BlockType type = blockType.get();
            int col = source.index();
            source.next(type.open().length());
            BlockToken token = (type instanceof BlockType.BlockTypeWith<?> t)
                ? BlockToken.open(t, source.nextUntilWs().text())
                : BlockToken.open(type);
            scopes.put(source.row(), col, token);
            return Optional.of(token);
        }
        return Optional.empty();
    }

    private List<BlockType> matches(char ch) {
        return types.stream().filter(type -> type.open().charAt(0) == ch).toList();
    }

}
