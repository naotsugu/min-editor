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

import com.mammb.code.editor.core.syntax.BlockScopes;
import com.mammb.code.editor.core.syntax.Trie;

/**
 * The shell syntax.
 * @author Naotsugu Kobayashi
 */
public class ShellSyntax extends BasicSyntax {

    /** The keywords. */
    private static final Trie keywords = Trie.of("""
        alias,autoload,bg,bindkey,break,builtin,case,cd,command,continue,coproc,
        declare,dirs,disown,do,done,echo,ef,elif,else,end,eq,esac,eval,exec,exit,
        export,false,fc,fg,fi,for,foreach,function,ge,getopts,gt,hash,history,if,
        in,jobs,kill,le,let,local,logout,lt,ne,nocorrect,nt,ot,popd,print,printf,
        pushd,pwd,read,readonly,repeat,return,select,set,shift,source,stat,suspend,
        test,then,time,times,trap,true,type,typeset,ulimit,umask,unalias,unhash,
        unlimit,unset,unsetopt,until,wait,which,while,while,$""");

    /**
     * Constructor.
     */
    public ShellSyntax() {
        super("shell",
            keywords,          // keywords
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            null,              // textBlock
            "#",               // lineComment
            null,              // blockComment
            ';'                // statementEnd
        );
    }

    public BlockScopes blockScopes() {
        return null;
    }

}
