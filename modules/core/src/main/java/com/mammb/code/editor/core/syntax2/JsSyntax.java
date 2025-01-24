package com.mammb.code.editor.core.syntax2;

import static com.mammb.code.editor.core.syntax2.BlockType.range;

public class JsSyntax extends BasicSyntax {

    public JsSyntax() {
        super("javascript", """
            abstract,arguments,await,boolean,break,byte,case,catch,char,class,const,continue,
            debugger,default,delete,do,double,else,enum,eval,export,extends,false,final,finally,
            float,for,function,goto,if,implements,import,in,instanceof,int,interface,let,long,
            native,new,null,package,private,protected,public,return,short,static,super,switch,
            synchronized,this,throw,throws,transient,true,try,typeof,var,void,volatile,while,
            with,yield""",
            '\\', // escapeChar
            '\'', // charLiteral
            '"',  // stringLiteral
            null, // textBlock
            "//", // lineComment
            range("/*", "*/"), // blockComment
            ';'   // statementEnd
        );
    }
}
