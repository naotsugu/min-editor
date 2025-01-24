package com.mammb.code.editor.core.syntax2;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;

public class RustSyntax extends BasicSyntax {

    public RustSyntax() {
        super("rust", """
            as,break,const,continue,crate,else,enum,extern,false,fn,for,if,impl,in,
            let,loop,match,mod,move,mut,pub,ref,return,self,Self,static,struct,super,
            trait,true,type,unsafe,use,where,while,async,await,dyn,try,
            abstract,become,box,do,final,macro,override,priv,typeof,unsized,virtual,yield""",
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
