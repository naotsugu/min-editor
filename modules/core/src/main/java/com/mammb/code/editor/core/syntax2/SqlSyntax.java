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

import static com.mammb.code.editor.core.syntax2.BlockType.range;

/**
 * The sql syntax.
 * @author Naotsugu Kobayashi
 */
public class SqlSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        add,all,alter,and,any,as,asc,backup,between,by,case,check,column,constraint,create,
        database,default,delete,desc,distinct,drop,exec,exists,foreign,from,full,group,
        having,in,index,insert,into,is,join,key,left,like,limit,not,null,or,order,outer,
        primary,procedure,replace,right,rownum,select,set,table,top,truncate,union,unique,
        update,values,view,where""");

    /** The blockComment. */
    private static final BlockType blockComment = range("/*", "*/");

    public SqlSyntax() {
        super("sql",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            null,              // textBlock
            "--",              // lineComment
            blockComment,      // blockComment
            ';'                // statementEnd
        );
    }
}

