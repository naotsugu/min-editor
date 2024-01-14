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
package com.mammb.code.editor.syntax.rust;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;
import com.mammb.code.editor.syntax.base.Trie;

import java.util.stream.Stream;

/**
 * Rust.
 * @author Naotsugu Kobayashi
 */
public class Rust {

    /** Rust Token type. */
    public interface RustToken extends com.mammb.code.editor.syntax.base.TokenType {

        TokenType KEYWORD = TokenType.build(Hue.DEEP_ORANGE);
        TokenType TEXT = TokenType.build(Hue.GREEN);
        TokenType NUMBER = TokenType.build(Hue.CYAN);
        TokenType CHAR_LITERAL = TokenType.build(Hue.TEAL);
        TokenType LINE_COMMENT = TokenType.build(Hue.DARK_GREY);
        TokenType COMMENT = TokenType.build(Hue.GREEN);
        TokenType DOC_COMMENT = TokenType.build(Hue.GREEN);

    }


    /**
     * Get the keyword trie.
     * @return the keyword trie
     */
    public static Trie keywords() {

        Trie trie = Trie.of();

        Stream.of("""
            as,break,const,continue,crate,else,enum,extern,false,fn,for,if,impl,in,
            let,loop,match,mod,move,mut,pub,ref,return,self,Self,static,struct,super,
            trait,true,type,unsafe,use,where,while,async,await,dyn,try,
            abstract,become,box,do,final,macro,override,priv,typeof,unsized,virtual,yield"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }


    /**
     * Determines if the character (Unicode code point) is permissible as the first character in an identifier.
     * @param cp the character (Unicode code point) to be tested
     * @return {@code true}, if the character may start an identifier
     */
    public static boolean isIdentifierStart(int cp) {
        int type = Character.getType(cp);
        return (type == Character.UPPERCASE_LETTER || type == Character.LOWERCASE_LETTER ||
            type == Character.TITLECASE_LETTER || type == Character.MODIFIER_LETTER ||
            type == Character.OTHER_LETTER || type == Character.LETTER_NUMBER);
    }


    /**
     * Determines if the specified character may be part of a rust identifier
     * as other than the first character.
     * @param cp the character to be tested
     * @return {@code true}, if the character may be part of a rust identifier
     */
    public static boolean isIdentifierPart(int cp) {
        int type = Character.getType(cp);
        return (type == Character.UPPERCASE_LETTER || type == Character.LOWERCASE_LETTER ||
            type == Character.TITLECASE_LETTER || type == Character.MODIFIER_LETTER ||
            type == Character.OTHER_LETTER ||  type == Character.LETTER_NUMBER ||
            type == Character.NON_SPACING_MARK ||  type == Character.COMBINING_SPACING_MARK ||
            type == Character.DECIMAL_DIGIT_NUMBER || type == Character.CONNECTOR_PUNCTUATION);
    }

}
