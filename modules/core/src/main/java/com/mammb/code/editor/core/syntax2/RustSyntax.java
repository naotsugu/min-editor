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
 * The rust syntax.
 * @author Naotsugu Kobayashi
 */
public class RustSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
            as,break,const,continue,crate,else,enum,extern,false,fn,for,if,impl,in,
            let,loop,match,mod,move,mut,pub,ref,return,self,Self,static,struct,super,
            trait,true,type,unsafe,use,where,while,async,await,dyn,try,
            abstract,become,box,do,final,macro,override,priv,typeof,unsized,virtual,yield""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /** The text block. */
    private static final BlockType textBlock = neutral("\"\"\"");

    /**
     * Constructor.
     */
    public RustSyntax() {
        super("rust",
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
