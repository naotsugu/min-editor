/*
 * Copyright 2023-2025 the original author or authors.
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

/**
 * The Ruby syntax.
 * @author Naotsugu Kobayashi
 */
public class RubySyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        __ENCODING__,__LINE__,__FILE__,BEGIN,END,alias,and,begin,break,case,class,def,defined?,
        do,else,elsif,end,ensure,false,for,if,in,module,next,nil,not,or,redo,rescue,retry,
        return,self,super,then,true,undef,unless,until,when,while,yield
        """);

    /** The blockComment. */
    private static final BlockType blockComment = BlockType.range("=begin", "=end");

    /**
     * Constructor.
     */
    public RubySyntax() {
        super("ruby",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            null,              // textBlock
            "#",               // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}
