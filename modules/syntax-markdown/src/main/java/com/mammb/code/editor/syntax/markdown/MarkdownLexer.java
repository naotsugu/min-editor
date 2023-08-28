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
package com.mammb.code.editor.syntax.markdown;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerProvider;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.TokenType;

import static com.mammb.code.editor.syntax.markdown.Markdown.MdToken.*;

/**
 * MarkdownLexer.
 * @author Naotsugu Kobayashi
 */
public class MarkdownLexer implements Lexer {

    /** The input string. */
    private LexerSource source;

    /** The lexer provider. */
    private LexerProvider lexerProvider;

    /**
     *
     * @param lexerProvider
     */
    public MarkdownLexer(LexerProvider lexerProvider) {
        this.lexerProvider = lexerProvider;
    }

    @Override
    public void setSource(LexerSource source) {
        this.source = source;
    }

    @Override
    public String name() {
        return "md";
    }


    @Override
    public Token nextToken() {

        if (source == null) {
            return Token.empty(null);
        }

        char ch = source.readChar();
        return switch (ch) {
            case ' ', '\t' -> Token.whitespace(source);
            case '\n', '\r' -> Token.lineEnd(source);
            case '#' -> readHeader(source);
            case '`' -> readFence(source);
            case 0 -> Token.empty(source);
            default -> Token.any(source);
        };
    }

    /**
     * Read header.
     * @param source the lexer source
     * @return the token
     */
    private Token readHeader(LexerSource source) {
        int pos = source.position();
        char[] ca = new char[] { source.peekChar(), source.peekChar(),
            source.peekChar(), source.peekChar(), source.peekChar()};

        TokenType type = null;
        if (ca[0] == ' ') type = H1;
        else if (ca[0] == '#' && ca[1] == ' ') type = H2;
        else if (ca[0] == '#' && ca[1] == '#' && ca[2] == ' ') type = H3;
        else if (ca[0] == '#' && ca[1] == '#' && ca[2] == '#' && ca[3] == ' ') type = H4;
        else if (ca[0] == '#' && ca[1] == '#' && ca[2] == '#' && ca[3] == '#' && ca[4] == ' ') type = H5;

        if (type != null) {
            for (;;) { if (source.peekChar() == '\n') break; }
            source.commitPeekBefore();
            return Token.of(type, Scope.INLINE_ANY, source.offset() + pos, source.position() + 1 - pos);
        } else {
            source.rollbackPeek();
            return Token.any(source);
        }
    }

    /**
     * Read fence.
     * @param source the lexer source
     * @return the token
     */
    private Token readFence(LexerSource source) {
        int pos = source.position();
        if (source.peekChar() == '`' && source.peekChar() == '`') {
            source.commitPeek();
            StringBuilder sb = new StringBuilder();
            for (;;) {
                char ch = source.peekChar();
                if (Character.isLetterOrDigit(ch)) {
                    sb.append(ch);
                } else if (ch == '\r' || ch == '\n' || ch == 0) {
                    source.commitPeekBefore();
                    return Token.of(FENCE, Scope.CONTEXT_START, source.offset() + pos, source.position() + 1 - pos, sb.toString());
                } else {
                    source.rollbackPeek();
                    return Token.of(FENCE, Scope.CONTEXT_ANY, source.offset() + pos, source.position() + 1 - pos);
                }
            }
        } else {
            source.rollbackPeek();
            return Token.of(CODE, Scope.INLINE_ANY, source.offset() + pos, 1);
        }
    }

}
