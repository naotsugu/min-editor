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

import static com.mammb.code.editor.core.syntax.BlockType.neutral;
import static com.mammb.code.editor.core.syntax.BlockType.range;

/**
 * The swift syntax.
 * @author Naotsugu Kobayashi
 */
public class SwiftSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        associatedtype,class,deinit,enum,extension,fileprivate,func,import,init,inout,internal,let,open,operator,
        private,precedencegroup,protocol,public,rethrows,static,struct,subscript,typealias,var,break,case,catch,
        continue,default,defer,do,else,fallthrough,for,guard,if,in,repeat,return,switch,throw,where,while,Any,as,
        await,false,is,nil,self,Self,super,true,try""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /** The text block. */
    private static final BlockType textBlock = neutral("\"\"\"");

    /**
     * Constructor.
     */
    public SwiftSyntax() {
        super("swift",
            keywords,          // keywords
            '\\',              // escapeChar
            (char) 0,          // charLiteral
            '"',               // stringLiteral
            textBlock,         // textBlock
            "//",              // lineComment
            blockComment,      // blockComment
            (char) 0           // statementEnd
        );
    }
}
