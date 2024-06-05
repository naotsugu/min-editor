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
package com.mammb.code.editor.syntax.sql;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;
import com.mammb.code.editor.syntax.base.Trie;

import java.util.stream.Stream;

/**
 * Sql.
 * @author Naotsugu Kobayashi
 */
public class Sql {

    /** Sql Token type. */
    public interface SqlToken extends com.mammb.code.editor.syntax.base.TokenType {

        TokenType KEYWORD = TokenType.build(Hue.DEEP_ORANGE);
        TokenType TEXT = TokenType.build(Hue.GREEN);
        TokenType NUMBER = TokenType.build(Hue.CYAN);
        TokenType LINE_COMMENT = TokenType.build(Hue.DARK_GREY);

    }


    /**
     * Get the keyword trie.
     * @return the keyword trie
     */
    public static Trie keywords() {

        Trie trie = Trie.of();

        Stream.of("""
            add,all,alter,and,any,as,asc,backup,between,by,case,check,column,
            constraint,create,database,default,delete,desc,distinct,drop,exec,
            exists,foreign,from,full,group,having,in,index,insert,into,is,join,
            key,left,like,limit,not,null,or,order,outer,primary,procedure,
            replace,right,rownum,select,set,table,top,truncate,union,unique,
            update,values,view,where"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }

}
