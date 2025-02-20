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
package com.mammb.code.editor.core.syntax.lang;

import com.mammb.code.editor.core.syntax.BlockType;
import com.mammb.code.editor.core.syntax.Trie;

import static com.mammb.code.editor.core.syntax.BlockType.range;

/**
 * The cpp syntax.
 * @author Naotsugu Kobayashi
 */
public class CppSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        alignas,alignof,and,and_eq,asm,auto,bitand,bitor,bool,
        break,case,catch,char,char8_t,char16_t,char32_t,class,
        compl,concept,const,consteval,constexpr,constinit,const_cast,
        continue,co_await,co_return,co_yield,decltype,default,
        delete,do,double,dynamic_cast,else,enum,explicit,export,
        extern,false,final,float,for,friend,goto,if,inline,int,
        long,mutable,namespace,new,noexcept,not,not_eq,nullptr,
        operator,or,or_eq,private,protected,public,register,
        reinterpret_cast,requires,return,short,signed,sizeof,
        static,static_assert,static_cast,struct,switch,template,
        this,thread_local,throw,true,try,typedef,typeid,typename,
        union,unsigned,using,virtual,void,volatile,wchar_t,while,
        xor,xor_eq""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /**
     * Constructor.
     */
    public CppSyntax() {
        super("cpp",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            null,              // textBlock
            "//",              // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}
