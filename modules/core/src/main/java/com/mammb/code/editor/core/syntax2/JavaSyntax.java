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

import com.mammb.code.editor.core.text.Style.StyleSpan;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mammb.code.editor.core.syntax2.LexerSources.*;
import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

/**
 * The Java syntax.
 * @author Naotsugu Kobayashi
 */
public class JavaSyntax implements Syntax {

    /** The keywords. */
    private final Trie keywords = Trie.of("""
        abstract,continue,for,new,switch,assert,default,goto,package,synchronized,boolean,do,if,private,
        this,break,double,implements,protected,throw,byte,else,import,public,throws,case,enum,instanceof,
        return,transient,catch,extends,int,short,try,char,final,interface,static,void,class,finally,long,
        strictfp,volatile,const,float,native,super,while,var,record,sealed,with,yield,to,transitive,uses
        """);

    /** The block comment. */
    private final BlockType blockComment = range("/*", "*/");
    /** The text block. */
    private final BlockType textBlock = neutral("\"\"\"");
    /** The block scopes. */
    private final BlockScopes blockScopes = new BlockScopes(blockComment, textBlock);

    @Override
    public String name() {
        return "java";
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

            if (ch == '/' && source.match("//")) {
                var s = source.nextRemaining();
                spans.add(new StyleSpan(Palette.gray, s.index(), s.length()));

            } else if (ch == '"') {
                readInlineBlock(source, '"', '\\', Palette.darkGreen).ifPresent(spans::add);

            } else if (ch == '\'') {
                readInlineBlock(source, '\'', '\\', Palette.darkPale).ifPresent(spans::add);

            } else if (ch == ';') {
                spans.add(new StyleSpan(Palette.darkOrange, peek.index(), 1));

            } else if (Character.isDigit(ch)) {
                readNumberLiteral(source, Palette.darkPale).ifPresent(spans::add);

            } else if (Character.isAlphabetic(ch)) {
                var s = source.nextUntil(Character::isUnicodeIdentifierPart);
                if (keywords.match(s.text())) {
                    var span = new StyleSpan(Palette.darkOrange, s.index(), s.length());
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
