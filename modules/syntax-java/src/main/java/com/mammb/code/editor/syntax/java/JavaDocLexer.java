/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.syntax.java;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.Trie;

import static com.mammb.code.editor.syntax.java.JavaDoc.JavaDocToken.*;

/**
 * JavaDocLexer.
 * @author Naotsugu Kobayashi
 */
public class JavaDocLexer implements Lexer {

    /** The block tags. */
    private static final Trie blockTags = JavaDoc.blockTags();

    /** The inline tags. */
    private static final Trie inlineTags = JavaDoc.inlineTags();

    /** The input string. */
    private LexerSource source;


    @Override
    public String name() {
        return "javadoc@java";
    }

    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
    }

    @Override
    public Token nextToken() {

        if (source == null) {
            return Token.empty(null);
        }

        char ch = source.readChar();
        return switch (ch) {
            case ' ', '\t'  -> Token.whitespace(source);
            case '\n', '\r' -> Token.lineEnd(source);
            case '@' -> readBlockTag(source);
            case '{' -> readInlineTag(source);
            case 0 -> Token.empty(source);
            default -> Token.any(source);
        };
    }


    private Token readBlockTag(LexerSource source) {

        int pos = source.position();
        StringBuilder sb = new StringBuilder();
        sb.append(source.currentChar());

        for (;;) {
            char ch = source.peekChar();
            if (!Character.isAlphabetic(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (blockTags.match(str)) {
                    return Token.of(TAG, Scope.NEUTRAL, source.offset() + pos, str.length());
                } else {
                    return Token.of(ANY, Scope.NEUTRAL, source.offset() + pos, str.length());
                }
            }
            sb.append(source.readChar());
        }
    }


    private Token readInlineTag(LexerSource source) {

        int pos = source.position();
        StringBuilder sb = new StringBuilder();

        for (;;) {
            char ch = source.peekChar();
            if (!Character.isAlphabetic(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (inlineTags.match(str)) {
                    return Token.of(TAG, Scope.NEUTRAL, source.offset() + pos, str.length());
                } else {
                    return Token.of(ANY, Scope.NEUTRAL, source.offset() + pos, str.length());
                }
            }
            sb.append(source.readChar());
        }
    }

}
