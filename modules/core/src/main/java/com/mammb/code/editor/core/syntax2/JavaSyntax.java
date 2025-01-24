package com.mammb.code.editor.core.syntax2;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

public class JavaSyntax extends BasicSyntax {

    public JavaSyntax() {
        super("java", """
            abstract,continue,for,new,switch,assert,default,goto,package,synchronized,boolean,do,if,private,
            this,break,double,implements,protected,throw,byte,else,import,public,throws,case,enum,instanceof,
            return,transient,catch,extends,int,short,try,char,final,interface,static,void,class,finally,long,
            strictfp,volatile,const,float,native,super,while,var,record,sealed,with,yield,to,transitive,uses""",
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
