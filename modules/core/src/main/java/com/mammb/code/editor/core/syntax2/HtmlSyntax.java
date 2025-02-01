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
package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.text.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mammb.code.editor.core.syntax2.BlockType.range;
import static com.mammb.code.editor.core.syntax2.LexerSources.readInlineBlock;

/**
 * The basic syntax.
 * @author Naotsugu Kobayashi
 */
public class HtmlSyntax implements Syntax {

    /** The blockComment. */
    private static final BlockType blockComment = range("<!--", "-->");
    /** The block scopes. */
    private final BlockScopes blockScopes = new BlockScopes(blockComment);


    @Override
    public String name() {
        return "html";
    }

    @Override
    public List<Style.StyleSpan> apply(int row, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<Style.StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            blockScopes.read(source).ifPresent(block ->
                spans.add(new Style.StyleSpan(Palette.gray, block.index(), block.length()))
            );
            if (!source.hasNext()) break;

            var peek = source.peek();

            if (peek.ch() == '<') {

                int n = source.match("</") ? 2 : 1;
                source.next(n);
                spans.add(new Style.StyleSpan(Palette.darkPale, peek.index(), n));

                var s = source.nextUntil(Character::isUnicodeIdentifierPart);
                if (!s.text().isEmpty()) {
                    spans.add(new Style.StyleSpan(Palette.darkOrange, s.index(), s.length()));
                }

            } else if (peek.ch() == '>') {
                spans.add(new Style.StyleSpan(Palette.darkPale, peek.index(), 1));
            } else if (peek.ch() == '"') {
                readInlineBlock(source, '"', '\\', Palette.darkGreen).ifPresent(spans::add);
            } else if (peek.ch() == '\'') {
                readInlineBlock(source, '\'', '\\', Palette.darkGreen).ifPresent(spans::add);
            }

            source.commitPeek();
        }

        return spans;
    }

    @Override
    public BlockScopes blockScopes() {
        return blockScopes;
    }

}
