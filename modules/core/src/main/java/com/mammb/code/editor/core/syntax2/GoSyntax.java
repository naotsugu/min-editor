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
package com.mammb.code.editor.core.syntax2;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

/**
 * The go syntax.
 * @author Naotsugu Kobayashi
 */
public class GoSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        break,default,func,interface,select,case,defer,go,map,struct,
        chan,else,goto,package,switch,const,fallthrough,if,range,type,
        continue,for,import,return,var,
        bool,int8,int16,int32,int64,uint8,uint16,uint32,uint64,
        float32,float64,complex64,complex128,byte,rune,uint,int,uintptr,string""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /** The text block. */
    private static final BlockType textBlock = neutral("`");

    /**
     * Constructor.
     */
    public GoSyntax() {
        super("go",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            textBlock,         // textBlock
            "//",              // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}
