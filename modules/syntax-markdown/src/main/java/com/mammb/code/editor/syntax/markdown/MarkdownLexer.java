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
package com.mammb.code.editor.syntax.markdown;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerProvider;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
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

    /** The scope. */
    private ScopeTree scope;

    /** The delegated lexer. */
    private Lexer delegate;


    /**
     * Constructor.
     * @param lexerProvider the lexer provider
     */
    public MarkdownLexer(LexerProvider lexerProvider) {
        this.lexerProvider = lexerProvider;
    }


    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
        this.scope = scope;
        if (delegate != null) {
            delegate.setSource(source, scope);
        }
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

        if (fenceDelegate()) {
            return delegate.nextToken();
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


    private boolean fenceDelegate() {

        var context = scope.current()
            .collect(n -> !n.open().context().isEmpty()).stream()
            .filter(n -> !n.open().context().equals(name())).findFirst()
            .map(n -> n.open().context()).orElse("");

        if (context.isEmpty() || source.matchLookahead('`', '`', '`')) {
            delegate = null;
            return false;
        }

        if (delegate == null || !delegate.name().equals(context)) {
            delegate = lexerProvider.get(context);
            delegate.setSource(source, scope);
        }
        return delegate != null;
    }


    /**
     * Read header.
     * @param source the lexer source
     * @return the token
     */
    private Token readHeader(LexerSource source) {

        int pos = source.position();
        char[] ca = source.lookahead(5);

        TokenType type = null;
        if (ca[0] == ' ') type = H1;
        else if (ca[0] == '#' && ca[1] == ' ') type = H2;
        else if (ca[0] == '#' && ca[1] == '#' && ca[2] == ' ') type = H3;
        else if (ca[0] == '#' && ca[1] == '#' && ca[2] == '#' && ca[3] == ' ') type = H4;
        else if (ca[0] == '#' && ca[1] == '#' && ca[2] == '#' && ca[3] == '#' && ca[4] == ' ') type = H5;

        if (type != null) {
            for (;;) {
                char ch = source.peekChar();
                if (ch == '\r' || ch == '\n' || ch == 0) break;
            }
            source.commitPeekBefore();
            return Token.of(type, Scope.INLINE_ANY, source.offset() + pos, source.position() + 1 - pos);
        } else {
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
                } else if (!sb.isEmpty()) {
                    source.commitPeekBefore();
                    return Token.of(FENCE, Scope.BLOCK_START, source.offset() + pos, source.position() + 1 - pos, sb.toString());
                } else {
                    source.rollbackPeek();
                    return Token.of(FENCE, Scope.BLOCK_ANY, source.offset() + pos, source.position() + 1 - pos);
                }
            }
        } else {
            source.rollbackPeek();
            return Token.of(CODE, Scope.INLINE_ANY, source.offset() + pos, 1);
        }
    }

}
