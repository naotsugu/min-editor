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
 * The javascript syntax.
 * @author Naotsugu Kobayashi
 */
public class JsSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
            abstract,arguments,await,boolean,break,byte,case,catch,char,class,const,continue,
            debugger,default,delete,do,double,else,enum,eval,export,extends,false,final,finally,
            float,for,function,goto,if,implements,import,in,instanceof,int,interface,let,long,
            native,new,null,package,private,protected,public,return,short,static,super,switch,
            synchronized,this,throw,throws,transient,true,try,typeof,var,void,volatile,while,
            with,yield""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    /**
     * Constructor.
     */
    public JsSyntax() {
        super("javascript",
            keywords,     // keywords
            '\\',         // escapeChar
            '\'',         // charLiteral
            '"',          // stringLiteral
            null,         // textBlock
            "//",         // lineComment
            blockComment, // blockComment
            ';'           // statementEnd
        );
    }
}
