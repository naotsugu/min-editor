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
package com.mammb.code.editor2.syntax.java;

import com.mammb.code.editor2.syntax.Lexer;
import com.mammb.code.editor2.syntax.Token;
import com.mammb.code.editor2.syntax.TokenType;
import com.mammb.code.editor2.syntax.Trie;

import java.util.stream.Stream;

/**
 * JavaLexer.
 * @author Naotsugu Kobayashi
 */
public class JavaLexer implements Lexer {

    /** Token type. */
    protected interface Type extends TokenType {
        TokenType KEYWORD = TokenType.create();
        TokenType TEXT = TokenType.create();
        TokenType NUMBER = TokenType.create();
        TokenType LINE_COMMENT = TokenType.create();
        TokenType COMMENT = TokenType.create();
        TokenType CHAR_LITERAL = TokenType.create();
    }

    /** The syntax keywords. */
    private static final Trie keywords = keywords();


    @Override
    public String name() {
        return "java";
    }

    @Override
    public Token nextToken() {
        return null;
    }


    /**
     * Get the keyword trie.
     * @return the keyword trie
     */
    private static Trie keywords() {
        Trie trie = Trie.of();
        Stream.of("""
        abstract,continue,for,new,switch,assert,default,goto,package,synchronized,boolean,do,if,private,
        this,break,double,implements,protected,throw,byte,else,import,public,throws,case,enum,instanceof,
        return,transient,catch,extends,int,short,try,char,final,interface,static,void,class,finally,long,
        strictfp,volatile,const,float,native,super,while,var,record,sealed,with,yield,to,transitive,uses"""
            .split("[,\\s]")).forEach(trie::put);
        return trie;
    }

}
