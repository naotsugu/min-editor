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

import static com.mammb.code.editor.syntax.java.Java.JavaToken.ANY;
import static com.mammb.code.editor.syntax.java.Java.JavaToken.CHAR_LITERAL;
import static com.mammb.code.editor.syntax.java.Java.JavaToken.COMMENT;
import static com.mammb.code.editor.syntax.java.Java.JavaToken.KEYWORD;
import static com.mammb.code.editor.syntax.java.Java.JavaToken.LINE_COMMENT;
import static com.mammb.code.editor.syntax.java.Java.JavaToken.NUMBER;
import static com.mammb.code.editor.syntax.java.Java.JavaToken.TEXT;

/**
 * JavaLexer.
 * @author Naotsugu Kobayashi
 */
public class JavaLexer implements Lexer {

    /** The syntax keywords. */
    private static final Trie keywords = Java.keywords();

    /** The input string. */
    private LexerSource source;

    /** The scope. */
    private ScopeTree scope;

    /** The delegate lexer. */
    private Lexer delegate;


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
        return "java";
    }


    @Override
    public Token nextToken() {

        if (source == null) {
            return Token.empty(null);
        }

        if (delegate()) {
            return delegate.nextToken();
        }

        char ch = source.readChar();
        return switch (ch) {
            case ' ', '\t'  -> Token.whitespace(source);
            case '\n', '\r' -> Token.lineEnd(source);
            case '/'  -> readComment(source);
            case '*'  -> readCommentBlockClosed(source);
            case '"'  -> readText(source);
            case '\'' -> readChar(source);
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-' -> readNumber(source);
            case 0  -> Token.empty(source);
            default -> Character.isJavaIdentifierStart(ch)
                ? readIdentifier(source)
                : Token.any(source);
        };
    }


    private boolean delegate() {

        var context = scope.current().select(n -> n.open().context().equals("javadoc"))
            .map(n -> n.open().context()).orElse("");

        if (!context.isEmpty() && !existsCommentBlockClosed() && (delegate == null || !delegate.name().equals(context))) {
            delegate = new JavaDocLexer();
            delegate.setSource(source, scope);
            return true;

        }
        delegate = null;
        return false;

    }


    /**
     * Read comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readComment(LexerSource source) {
        int pos = source.position();
        char ch = source.peekChar();
        if (ch == '/') {
            source.commitPeek();
            return Token.of(LINE_COMMENT, Scope.INLINE_START, source.offset() + pos, 2);
        } else if (ch == '*') {
            source.commitPeek();
            if (source.peekChar() == '*') {
                source.commitPeek();
                return Token.of(COMMENT, Scope.BLOCK_START, source.offset() + pos, 3, "javadoc");
            }
            source.rollbackPeek();
            return Token.of(COMMENT, Scope.BLOCK_START, source.offset() + pos, 2);
        } else {
            return Token.any(source);
        }
    }


    /**
     * Read block comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readCommentBlockClosed(LexerSource source) {
        int pos = source.position();
        char ch = source.peekChar();
        if (ch == '/') {
            source.commitPeek();
            return Token.of(COMMENT, Scope.BLOCK_END, source.offset() + pos, 2);
        } else {
            return Token.any(source);
        }
    }


    /**
     * Read text.
     * @param source the lexer source
     * @return the token
     */
    private Token readText(LexerSource source) {
        if (source.peekChar() == '"' && source.peekChar() == '"') {
            source.commitPeek();
            return Token.of(TEXT, Scope.BLOCK_ANY, source.offset() + source.position() - 3, 3);
        }
        source.rollbackPeek();
        return readString(source);
    }


    /**
     * Read string.
     * @param source the lexer source
     * @return the token
     */
    private Token readString(LexerSource source) {
        int pos = source.position();
        char prev = 0;
        for (;;) {
            char ch = source.peekChar();
            if (ch < ' ') {
                source.rollbackPeek();
                return Token.any(source);
            } else if (prev != '\\' && ch == '"') {
                source.commitPeek();
                return Token.of(TEXT, Scope.NEUTRAL, source.offset() + pos, source.position() + 1 - pos);
            }
            prev = ch;
        }
    }


    /**
     * Read char.
     * @param source the lexer source
     * @return the token
     */
    private Token readChar(LexerSource source) {
        int pos = source.position();
        char prev = 0;
        for (;;) {
            char ch = source.peekChar();
            if (ch < ' ') {
                source.rollbackPeek();
                return Token.any(source);
            } else if (prev != '\\' && ch == '\'') {
                source.commitPeek();
                return Token.of(CHAR_LITERAL, Scope.NEUTRAL, source.offset() + pos, source.position() + 1 - pos);
            }
            prev = ch;
        }
    }


    /**
     * Read the number.
     * @param source the lexer source
     * @return the token
     */
    private Token readNumber(LexerSource source) {
        var sb = new StringBuilder();
        sb.append(source.currentChar());
        int pos = source.position();
        for (;;) {
            char ch = source.peekChar();
            if (Java.isJavaNumberPart(ch)) {
                sb.append(ch);
            } else {
                break;
            }
        }
        if (Java.isJavaNumber(sb.toString())) {
            source.commitPeekBefore();
            return Token.of(NUMBER, Scope.NEUTRAL, source.offset() + pos, sb.length());
        } else {
            return Token.any(source);
        }
    }


    /**
     * Read identifier.
     * @param source the lexer source
     * @return the token
     */
    private Token readIdentifier(LexerSource source) {

        int pos = source.position();
        StringBuilder sb = new StringBuilder();
        sb.append(source.currentChar());

        for (;;) {
            char ch = source.peekChar();
            if (!Character.isJavaIdentifierPart(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (keywords.match(str)) {
                    return Token.of(KEYWORD, Scope.NEUTRAL, source.offset() + pos, str.length());
                } else {
                    return Token.of(ANY, Scope.NEUTRAL, source.offset() + pos, str.length());
                }
            }
            sb.append(source.readChar());
        }
    }


    private boolean existsCommentBlockClosed() {
        char[] ch = new char[] { source.peekChar(), source.peekChar() };
        source.rollbackPeek();
        return ch[0] == '*' && ch[1] == '/';
    }

}
