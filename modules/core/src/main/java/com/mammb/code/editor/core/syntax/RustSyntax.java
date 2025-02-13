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
import com.mammb.code.editor.core.text.Style;

/**
 * The rust syntax.
 * @author Naotsugu Kobayashi
 */
public class RustSyntax implements Syntax {

    /** The keywords. */
    private final Trie keywords = Trie.of("""
        as,break,const,continue,crate,else,enum,extern,false,fn,for,if,impl,in,
        let,loop,match,mod,move,mut,pub,ref,return,self,Self,static,struct,super,
        trait,true,type,unsafe,use,where,while,async,await,dyn,try,
        abstract,become,box,do,final,macro,override,priv,typeof,unsized,virtual,yield
        """);
    static final BlockScopes.Range blockComment = BlockScopes.BlockType.range("/*", "*/");
    private final BlockScopes scopes = new BlockScopes();

    @Override
    public String name() {
        return "rust";
    }

    @Override
    public List<Style.StyleSpan> apply(int row, String text) {

        scopes.clear(row);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        var spans = new ArrayList<Style.StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            var peek = source.peek();
            char ch = peek.ch();
            Optional<BlockScopes.BlockType> block = scopes.inScope(source.row(), peek.index());

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
                var span = new Style.StyleSpan(Palette.gray, s.index(), s.length());
                spans.add(span);

            } else if (ch == '"' && !source.match("\"\"\"")) {
                var span = source.readInlineBlock('"', '\\', Palette.darkGreen);
                if (span != null) spans.add(span);

            } else if (ch == '\'') {
                var span = source.readInlineBlock('\'', '\\', Palette.darkPale);
                if (span != null) spans.add(span);

            } else if (ch == ';') {
                var span = new Style.StyleSpan(Palette.darkOrange, peek.index(), 1);
                spans.add(span);

            } else if (Character.isDigit(ch)) {
                var span = source.readNumberLiteral(Palette.darkPale);
                if (span != null) spans.add(span);

            } else if (isIdentifierStart(ch)) {
                var s = source.nextIdentifierPart();
                if (keywords.match(s.string())) {
                    var span = new Style.StyleSpan(Palette.darkOrange, s.index(), s.length());
                    spans.add(span);
                }
            }
            source.commitPeek();
        }

        return spans;
    }


    public static boolean isIdentifierStart(int cp) {
        return cp == '_' || Syntax.isXidStart(cp);
    }

    public static boolean isIdentifierPart(int cp) {
        return Syntax.isXidContinue(cp);
    }

}
