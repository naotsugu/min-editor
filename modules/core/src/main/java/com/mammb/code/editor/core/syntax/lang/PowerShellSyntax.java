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

import static com.mammb.code.editor.core.syntax.BlockType.range;

/**
 * The PowerShell syntax.
 * @author Naotsugu Kobayashi
 */
public class PowerShellSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
            begin,break,catch,class,clean,continue,data,define,do,dynamicparam,else,elseif,
            end,enum,exit,filter,finally,for,foreach,from,function,hidden,if,in,param,
            process,return,static,switch,throw,trap,try,until,using,var,while,
            inlinescript,parallel,sequence,workflow,
            $true,$false,$null""");

    /** The blockComment. */
    private static final BlockType blockComment = range("<#", "#>");

    /** The text block. */
    private static final BlockType textBlock = range("@\"", "\"@");

    /**
     * Constructor.
     */
    public PowerShellSyntax() {
        super("ps1",
            keywords,          // keywords
            '`',               // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            textBlock,         // textBlock
            "#",               // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}
