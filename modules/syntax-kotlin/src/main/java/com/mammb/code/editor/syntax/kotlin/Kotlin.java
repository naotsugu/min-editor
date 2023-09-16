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

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;
import com.mammb.code.editor.syntax.base.Trie;

import java.util.stream.Stream;

/**
 * Kotlin.
 * @author Naotsugu Kobayashi
 */
public class Kotlin {

    /** Kotlin Token type. */
    public interface KotlinToken extends com.mammb.code.editor.syntax.base.TokenType {

        TokenType KEYWORD = TokenType.build(Hue.DEEP_ORANGE);
        TokenType MODIFIER = TokenType.build(Hue.ORANGE);
        TokenType TEXT = TokenType.build(Hue.GREEN);
        TokenType NUMBER = TokenType.build(Hue.CYAN);
        TokenType CHAR_LITERAL = TokenType.build(Hue.TEAL);
        TokenType LINE_COMMENT = TokenType.build(Hue.BLUE_GREY);
        TokenType COMMENT = TokenType.build(Hue.GREEN);
        TokenType DOC_COMMENT = TokenType.build(Hue.GREEN);

    }

    /**
     * Get the keyword trie.
     * @return the keyword trie
     */
    public static Trie keywords() {

        Trie trie = Trie.of();

        Stream.of("""
            as,as?,break,class,continue,do,else,false,for,fun,if,in,!in,interface,is,!is,null,
            object,package,return,super,this,throw,true,try,typealias,typeof,val,var,when,while,
            by,catch,constructor,delegate,dynamic,field,file,finally,get,import,init,param,
            property,receiver,set,setparam,value,where,
            it"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }

    /**
     * Get the modifier keywords trie.
     * @return the modifier keywords trie
     */
    public static Trie modifiers() {

        Trie trie = Trie.of();

        Stream.of("""
            abstract,actual,annotation,companion,const,crossinline,data,enum,expect,external,
            final,infix,inline,inner,internal,lateinit,noinline,open,operator,out,override,
            private,protected,public,reified,sealed,suspend,tailrec,vararg"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }

}
