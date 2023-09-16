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
package com.mammb.code.editor.syntax.kotlin;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.Trie;

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
            default -> Token.any(source);
        };
    }

}
