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
package com.mammb.code.editor.syntax.python;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;
import com.mammb.code.editor.syntax.base.Trie;

import java.util.stream.Stream;

/**
 * Python.
 * @author Naotsugu Kobayashi
 */
public class Python {

    /** Python Token type. */
    public interface PythonToken extends com.mammb.code.editor.syntax.base.TokenType {

        TokenType KEYWORD = TokenType.build(Hue.DEEP_ORANGE);
        TokenType LINE_COMMENT = TokenType.build(Hue.DARK_GREY);

    }


    /**
     * Get the keyword trie.
     * @return the keyword trie
     */
    public static Trie keywords() {

        Trie trie = Trie.of();

        Stream.of("""
            False,await,else,import,pass,None,break,except,in,raise,True,class,finally,is,return,
            and,continue,for,lambda,try,as,def,from,nonlocal,while,assert,del,global,not,with,
            async,elif,if,or,yield"""
            .split("[,\\s]")).forEach(trie::put);

        Stream.of("""
            match, case"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }


    /**
     * Determines if the character (Unicode code point) is permissible as the first character in an identifier.
     * <p>
     *   id_start ::= <all characters in general categories Lu, Ll, Lt, Lm, Lo, Nl, the underscore, and characters with the Other_ID_Start property>
     * </p>
     * @param cp the character (Unicode code point) to be tested
     * @return {@code true}, if the character may start an identifier
     */
    public static boolean isIdentifierStart(int cp) {
        int type = Character.getType(cp);
        return Character.isUnicodeIdentifierStart(cp)
            || type == Character.UPPERCASE_LETTER // Lu
            || type == Character.LOWERCASE_LETTER // Ll
            || type == Character.TITLECASE_LETTER // Lt
            || type == Character.MODIFIER_LETTER  // Lm
            || type == Character.OTHER_LETTER     // Lo
            || type == Character.LETTER_NUMBER    // Nl
            || cp == '_';
    }


    /**
     * Determines if the specified character may be part of a rust identifier
     * as other than the first character.
     * <p>
     *  xid_continue ::= <all characters in id_continue whose NFKC normalization is in "id_continue*">
     * </p>
     * @param cp the character to be tested
     * @return {@code true}, if the character may be part of a rust identifier
     */
    public static boolean isIdentifierPart(int cp) {
        // Normalizer.normalize(string, Normalizer.Form.NFKC);
        return Character.isUnicodeIdentifierPart(cp);
    }

}
