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
package com.mammb.code.editor.syntax.rust;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.Trie;

import static com.mammb.code.editor.syntax.rust.Rust.RustToken.COMMENT;
import static com.mammb.code.editor.syntax.rust.Rust.RustToken.DOC_COMMENT;
import static com.mammb.code.editor.syntax.rust.Rust.RustToken.KEYWORD;
import static com.mammb.code.editor.syntax.rust.Rust.RustToken.LINE_COMMENT;
import static com.mammb.code.editor.syntax.rust.Rust.RustToken.TEXT;
import static com.mammb.code.editor.syntax.rust.Rust.RustToken.ANY;

/**
 * RustLexer.
 * @author Naotsugu Kobayashi
 */
public class RustLexer implements Lexer {

    /** The syntax keywords. */
    private static final Trie keywords = Rust.keywords();

    /** The input string. */
    private LexerSource source;

    /** The scope. */
    private ScopeTree scope;


    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
        this.scope = scope;
    }

    @Override
    public String name() {
        return "rust";
    }


    @Override
    public Token nextToken() {

        if (source == null) {
            return Token.empty(null);
        }

        int ch = source.readChar();
        if (Character.isHighSurrogate((char) ch)) {
            ch = Character.toCodePoint((char) ch, source.currentChar());
        }

        return switch (ch) {
            case ' ', '\t'  -> Token.whitespace(source);
            case '\n', '\r' -> Token.lineEnd(source);
            case 0    -> Token.empty(source);
            case '/' -> readComment(source);
            case '*'  -> readCommentBlockClosed(source);
            case '"'  -> readString(source);
            default -> Rust.isIdentifierStart(ch)
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
            char nch = source.peekChar();
            if (nch == '/' || nch == '!') {
                source.commitPeek();
                return Token.of(DOC_COMMENT, Scope.INLINE_START, pos, 3);
            } else {
                source.commitPeek();
                return Token.of(LINE_COMMENT, Scope.INLINE_START, pos, 2);
            }
        } else if (ch == '*') {
            source.commitPeek();
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
        char ch = source.peekChar();
        if (ch == '/') {
            source.commitPeek();
            return Token.of(COMMENT, Scope.BLOCK_END, pos, 2);
        } else {
            return Token.any(source);
        }
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
            if (!Rust.isIdentifierPart(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (keywords.match(str)) {
                    return Token.of(KEYWORD, Scope.NEUTRAL, pos, str.length());
                } else {
                    return Token.of(ANY, Scope.NEUTRAL, pos, str.length());
                }
            }
            sb.append(source.readChar());
        }
    }

}
