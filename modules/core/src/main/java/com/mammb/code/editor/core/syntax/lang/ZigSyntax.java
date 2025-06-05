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

import com.mammb.code.editor.core.syntax.Trie;

/**
 * The zig syntax.
 * @author Naotsugu Kobayashi
 */
public class ZigSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        addrspace,align,allowzero,and,anyframe,anytype,asm,async,
        await,break,callconv,catch,comptime,const,continue,defer,
        else,enum,errdefer,error,export,extern,fn,for,if,inline,
        linksection,noalias,noinline,nosuspend,opaque,or,orelse,
        packed,pub,resume,return,struct,suspend,switch,test,threadlocal,
        try,union,unreachable,usingnamespace,var,volatile,while""");

    /**
     * Constructor.
     */
    public ZigSyntax() {
        super("zig",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            null,              // textBlock
            "//",              // lineComment
            null,              // blockComment
            ';'                // statementEnd
        );
    }

}
