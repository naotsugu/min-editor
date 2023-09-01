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
package com.mammb.code.editor.syntax.base.impl;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.TokenType;

/**
 * PassThrough Lexer.
 * @author Naotsugu Kobayashi
 */
public class PassThroughLexer implements Lexer {

    /** The name. */
    private final String name;

    /** The input string. */
    private LexerSource source;

    /** The string length. */
    private int length = 0;


    /**
     * Constructor.
     * @param name the name
     */
    public PassThroughLexer(String name) {
        this.name = name;
    }


    @Override
    public String name() {
        return name;
    }


    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
        this.length = source.length();
    }


    @Override
    public Token nextToken() {
        if (source != null && source.length() > 0) {
            length = source.length();
            source = null;
            return Token.of(TokenType.ANY, Scope.NEUTRAL, 0, length);
        } else {
            return Token.of(TokenType.EMPTY, Scope.NEUTRAL, length, 0);
        }
    }

}
