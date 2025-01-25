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

/**
 * The python syntax.
 * @author Naotsugu Kobayashi
 */
public class PythonSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        False,await,else,import,pass,None,break,except,in,raise,True,class,finally,is,return,
        and,continue,for,lambda,try,as,def,from,nonlocal,while,assert,del,global,not,with,
        async,elif,if,or,yield
        match, case""");

    /** The blockComment. */
    private static final BlockType blockComment = neutral("\"\"\"");
    /** The blockComment2. */
    private static final BlockType blockComment2 = neutral("'''");

    public PythonSyntax() {
        super("python",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            blockComment2,     // textBlock
            "#",               // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}
