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
package com.mammb.code.editor.syntax.kotlin;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.Trie;

import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.ANY;
import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.CHAR_LITERAL;
import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.COMMENT;
import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.KEYWORD;
import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.LINE_COMMENT;
import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.MODIFIER;
import static com.mammb.code.editor.syntax.kotlin.Kotlin.KotlinToken.TEXT;

/**
 * KotlinLexer.
 * @author Naotsugu Kobayashi
 */
public class KotlinLexer implements Lexer {

    /** The syntax keywords. */
    private static final Trie keywords = Kotlin.keywords();

    /** The modifier keywords. */
    private static final Trie modifiers = Kotlin.modifiers();

    /** The input string. */
    private LexerSource source;

    /** The scope. */
    private ScopeTree scope;


    @Override
    public String name() {
        return "kotlin";
    }


    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
        this.scope = scope;
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
            case 0  -> Token.empty(source);
            case '/'  -> readComment(source);
            case '*'  -> readCommentBlockClosed(source);
            case '"'  -> readText(source);
            case '\'' -> readChar(source);
            default -> Kotlin.isIdentifierStart(ch)
                ? readIdentifier(source, ch)
                : Token.any(source);
        };
    }

    /**
     * Read comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readComment(LexerSource source) {
        long pos = source.offset() + source.position();
        char ch = source.peekChar();
        if (ch == '/') {
            source.commitPeek();
            return Token.of(LINE_COMMENT, Scope.INLINE_START, pos, 2);
        } else if (ch == '*') {
            source.commitPeek();
            if (source.peekChar() == '*') {
                source.commitPeek();
                return Token.of(COMMENT, Scope.BLOCK_START, pos, 3);
            }
            source.rollbackPeek();
            return Token.of(COMMENT, Scope.BLOCK_START, pos, 2);
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
        long pos = source.offset() + source.position();
        if (source.peekChar() == '/') {
            source.commitPeek();
            return Token.of(COMMENT, Scope.BLOCK_END, pos, 2);
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
        long pos = source.offset() + source.position();
        if (source.peekChar() == '"' && source.peekChar() == '"') {
            source.commitPeek();
            return Token.of(TEXT, Scope.BLOCK_ANY, pos, 3);
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
     * Read identifier.
     * @param source the lexer source
     * @return the token
     */
    private Token readIdentifier(LexerSource source, int cp) {

        long pos = source.offset() + source.position();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toChars(cp));

        for (;;) {
            char ch = source.peekChar();
            if (!Kotlin.isIdentifierPart(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (keywords.match(str)) {
                    return Token.of(KEYWORD, Scope.NEUTRAL, pos, str.length());
                } else if (modifiers.match(str)) {
                    return Token.of(MODIFIER, Scope.NEUTRAL, pos, str.length());
                } else {
                    return Token.of(ANY, Scope.NEUTRAL, pos, str.length());
                }
            }
            sb.append(source.readChar());
        }
    }

}
