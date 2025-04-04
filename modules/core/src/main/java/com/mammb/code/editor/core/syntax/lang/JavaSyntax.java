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

import static com.mammb.code.editor.core.syntax.BlockType.neutral;
import static com.mammb.code.editor.core.syntax.BlockType.range;

/**
 * The java syntax.
 * @author Naotsugu Kobayashi
 */
public class JavaSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
            abstract,continue,for,new,switch,assert,default,goto,package,synchronized,boolean,do,if,private,
            this,break,double,implements,protected,throw,byte,else,import,public,throws,case,enum,instanceof,
            return,transient,catch,extends,int,short,try,char,final,interface,static,void,class,finally,long,
            strictfp,volatile,const,float,native,super,while,var,record,sealed,with,yield,to,transitive,uses""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /** The text block. */
    private static final BlockType textBlock = neutral("\"\"\"");

    /**
     * Constructor.
     */
    public JavaSyntax() {
        super("java",
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
