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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.syntax.BlockScopes.BlockType;
import com.mammb.code.editor.core.text.Style.StyleSpan;

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
    static final BlockScopes.Range blockComment = BlockType.range("/*", "*/");
    static final BlockScopes.Neutral textBlock = BlockType.neutral("\"\"\"");

    private final BlockScopes scopes = new BlockScopes();


    @Override
    public String name() {
        return "java";
    }


    @Override
    public List<StyleSpan> apply(int row, String text) {

        scopes.clear(row);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            var peek = source.peek();
            char ch = peek.ch();
            Optional<BlockType> block = scopes.inScope(source.row(), peek.index());

            if (block.filter(t -> t == blockComment).isPresent()) {
                var span = source.readBlockClose(scopes, blockComment, Palette.darkGreen);
                spans.add(span);

            } else if (ch == '/' && source.match("/*")) {
                scopes.putOpen(source.row(), peek.index(), blockComment);
                var span = source.readBlockClose(scopes, blockComment, Palette.darkGreen);
                spans.add(span);

            } else if (ch == '*' && source.match("*/")) {
                scopes.putClose(source.row(), peek.index(), blockComment);

            } else if (ch == '/' && source.match("//")) {
                var s = source.nextRemaining();
                var span = new StyleSpan(Palette.gray, s.index(), s.length());
                spans.add(span);

            } else if (ch == '"' && !source.match("\"\"\"")) {
                var span = source.readInlineBlock('"', '\\', Palette.darkGreen);
                if (span != null) spans.add(span);

            } else if (ch == '\'') {
                var span = source.readInlineBlock('\'', '\\', Palette.darkPale);
                if (span != null) spans.add(span);

            } else if (ch == ';') {
                var span = new StyleSpan(Palette.darkOrange, peek.index(), 1);
                spans.add(span);

            } else if (Character.isDigit(ch)) {
                var span = source.readNumberLiteral(Palette.darkPale);
                if (span != null) spans.add(span);

            } else if (Character.isAlphabetic(ch)) {
                var s = source.nextIdentifierPart();
                if (keywords.match(s.string())) {
                    var span = new StyleSpan(Palette.darkOrange, s.index(), s.length());
                    spans.add(span);
                }
            }
            source.commitPeek();
        }

        return spans;
    }

}
