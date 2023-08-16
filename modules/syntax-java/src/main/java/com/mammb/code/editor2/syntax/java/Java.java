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

import com.mammb.code.editor2.syntax.TokenType;
import com.mammb.code.editor2.syntax.Trie;
import java.util.stream.Stream;

/**
 * Java.
 * @author Naotsugu Kobayashi
 */
public class Java {

    public interface ToKenType extends com.mammb.code.editor2.syntax.TokenType {

        TokenType KEYWORD = TokenType.create("#eb8a3a");
        TokenType TEXT = TokenType.create("#629755");
        TokenType NUMBER = TokenType.create("#78aed7");
        TokenType LINE_COMMENT = TokenType.create("#808080");
        TokenType COMMENT = TokenType.create("#629755");
        TokenType CHAR_LITERAL = TokenType.create("#629755");

    }


    /**
     * Get the keyword trie.
     * @return the keyword trie
     */
    public static Trie keywords() {
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
