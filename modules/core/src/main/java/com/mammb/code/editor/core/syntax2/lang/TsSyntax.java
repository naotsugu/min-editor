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
package com.mammb.code.editor.core.syntax2.lang;

import com.mammb.code.editor.core.syntax2.BlockType;
import com.mammb.code.editor.core.syntax2.Trie;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

/**
 * The type script syntax.
 * @author Naotsugu Kobayashi
 */
public class TsSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        break,case,catch,class,const,continue,debugger,default,delete,
        do,else,enum,export,extends,false,finally,for,function,if,
        import,in,instanceof,new,null,return,super,switch,this,throw,
        true,try,typeof,var,void,while,with,
        as,implements,interface,let,package,private,protected,public,
        static,yield,
        any,boolean,constructor,declare,get,module,require,number,
        set,string,symbol,type,from,of""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /** The text block. */
    private static final BlockType templateLiteral = neutral("`");

    /**
     * Constructor.
     */
    public TsSyntax() {
        super("typescript",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            templateLiteral,   // textBlock
            "//",              // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}
