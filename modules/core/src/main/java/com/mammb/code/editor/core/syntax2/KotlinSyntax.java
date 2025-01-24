package com.mammb.code.editor.core.syntax2;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

public class KotlinSyntax extends BasicSyntax {

    public KotlinSyntax() {
        super("kotlin", """
            as,as?,break,class,continue,do,else,false,for,fun,if,in,!in,interface,is,!is,null,
            object,package,return,super,this,throw,true,try,typealias,typeof,val,var,when,while,
            by,catch,constructor,delegate,dynamic,field,file,finally,get,import,init,param,
            property,receiver,set,setparam,value,where,it""",
            '\\',              // escapeChar
            '\'',              // charLiteral
            '"',               // stringLiteral
            neutral("\"\"\""), // textBlock
            "//",              // lineComment
            range("/*", "*/"), // blockComment
            ';'                // statementEnd
        );
    }
}
