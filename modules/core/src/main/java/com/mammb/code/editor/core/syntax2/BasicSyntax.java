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

import com.mammb.code.editor.core.text.Style.StyleSpan;
import com.mammb.code.editor.core.text.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mammb.code.editor.core.syntax2.LexerSources.readInlineBlock;
import static com.mammb.code.editor.core.syntax2.LexerSources.readNumberLiteral;

/**
 * The basic syntax.
 * @author Naotsugu Kobayashi
 */
public class BasicSyntax implements Syntax {

    private final String name;
    private final Trie keywords;
    private final char escapeChar;
    private final char charLiteral;
    private final char stringLiteral;
    private final BlockType textBlock;
    private final String lineComment;
    private final BlockType blockComment;
    private final char statementEnd;

    /** The block scopes. */
    private final BlockScopes blockScopes;

    public BasicSyntax(
            String name,
            String keywords,
            char escapeChar,
            char charLiteral,
            char stringLiteral,
            BlockType textBlock,
            String lineComment,
            BlockType blockComment,
            char statementEnd) {
        this.name = name;
        this.keywords = Trie.of(keywords);
        this.escapeChar = escapeChar;
        this.charLiteral = charLiteral;
        this.stringLiteral = stringLiteral;
        this.textBlock = textBlock;
        this.lineComment = lineComment;
        this.blockComment = blockComment;
        this.statementEnd = statementEnd;

        this.blockScopes = new BlockScopes(blockComment, textBlock);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<StyleSpan> apply(int row, String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            blockScopes.read(source).ifPresent(block ->
                spans.add(new StyleSpan(Palette.darkGreen, block.index(), block.length()))
            );
            if (!source.hasNext()) break;

            var peek = source.peek();
            char ch = peek.ch();

            if (ch == lineComment.charAt(0) && source.match(lineComment)) {
                var s = source.nextRemaining();
                spans.add(new Style.StyleSpan(Palette.gray, s.index(), s.length()));

            } else if (stringLiteral > 0 && ch == stringLiteral) {
                readInlineBlock(source, stringLiteral, escapeChar, Palette.darkGreen).ifPresent(spans::add);

            } else if (charLiteral > 0 && ch == charLiteral) {
                readInlineBlock(source, charLiteral, escapeChar, Palette.darkPale).ifPresent(spans::add);

            } else if (statementEnd > 0 && ch == statementEnd) {
                spans.add(new Style.StyleSpan(Palette.darkOrange, peek.index(), 1));

            } else if (Character.isDigit(ch)) {
                readNumberLiteral(source, Palette.darkPale).ifPresent(spans::add);

            } else if (Character.isAlphabetic(ch)) {
                var s = source.nextUntil(Character::isUnicodeIdentifierPart);
                if (keywords.match(s.text())) {
                    var span = new Style.StyleSpan(Palette.darkOrange, s.index(), s.length());
                    spans.add(span);
                }
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