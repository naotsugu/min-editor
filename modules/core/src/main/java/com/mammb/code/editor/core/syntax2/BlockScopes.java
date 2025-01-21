/*
 * Copyright 2022-2024 the original author or authors.
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

import com.mammb.code.editor.core.syntax.Syntax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

public class BlockScopes {

    BlockType blockComment = range("/*", "*/");
    BlockType fence = neutral("```", Syntax::of);

    private final ScopeStack scopes = new ScopeStack();
    private final List<BlockType> types = new ArrayList<>();

    public BlockScopes(BlockType... blockTypes) {
        types.addAll(List.of(blockTypes));
    }

    public void put(Iterator<LexerSource> sources) {

        if (!sources.hasNext()) return;

        LexerSource source = sources.next();
        scopes.clear(source.row());

        for (;;) {

            var maybeOpen = scopes.current();
            if (maybeOpen.isPresent()) {
                readUntilClose(maybeOpen.get().type(), source);
            }
            readUntilOpen(source);

            if (!source.hasNext()) {
                if (!sources.hasNext()) break;
                source = sources.next();
            }
        }
    }

    private void readUntilClose(BlockType type, LexerSource source) {

    }

    private void readUntilOpen(LexerSource source) {

        while (source.hasNext()) {
            var peek = source.peek();

            Optional<BlockType> blockType = matches(peek.ch()).stream()
                .filter(type -> source.match(type.open()))
                .findFirst();

            if (blockType.isPresent()) {
                BlockType type = blockType.get();
                int col = source.next(type.open().length()).index();
                if (type instanceof BlockType.BlockTypeWith<?> t) {
                    scopes.put(source.row(), col, BlockToken.open(t, source.nextUntilWs().text()));
                } else {
                    scopes.put(source.row(), col, BlockToken.open(type));
                }
                return;
            }
            source.commitPeek();
        }
    }

    private List<BlockType> matches(char ch) {
        return types.stream().filter(type -> type.open().charAt(0) == ch).toList();
    }

}
