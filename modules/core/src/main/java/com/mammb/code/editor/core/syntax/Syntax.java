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
package com.mammb.code.editor.core.syntax;

import com.mammb.code.editor.core.text.Style.StyleSpan;
import java.util.List;

/**
 * The syntax.
 * @author Naotsugu Kobayashi
 */
public interface Syntax {

    /**
     * Get the name
     * @return the name
     */
    String name();

    /**
     * Apply syntax highlights
     * @param row the number of row
     * @param text the row text
     * @return the list of StyleSpan
     */
    List<StyleSpan> apply(int row, String text);


    static Syntax of(String name) {
        return switch (name.toLowerCase()) {
            case "java" -> new JavaSyntax();
            case "md" -> new MarkdownSyntax();
            case "sql" -> new SqlSyntax();
            case "py" -> new PythonSyntax();
            case "js", "json" -> new JsSyntax();
            case "kotlin", "kt", "kts" -> new KotlinSyntax();
            default -> new PassThrough(name);
        };
    }

    record PassThrough(String name) implements Syntax {
        @Override
        public List<StyleSpan> apply(int row, String text) {
            return List.of();
        }
    }

    /**
     * Determines if the character (Unicode code point) is permissible
     * as the first character in an identifier.
     * <p>
     * Letter L :
     *   lowercase Ll
     *   modifier Lm,
     *   titlecase Lt,
     *   uppercase Lu,
     *   other Lo.
     * </p>
     * @param cp the character (Unicode code point) to be tested
     * @return {@code true}, if the character may start an identifier
     */
    static boolean isXidStart(int cp) {
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
    static boolean isXidContinue(int cp) {
        int type = Character.getType(cp);
        return (type == Character.UPPERCASE_LETTER || type == Character.LOWERCASE_LETTER ||
                type == Character.TITLECASE_LETTER || type == Character.MODIFIER_LETTER ||
                type == Character.OTHER_LETTER ||  type == Character.LETTER_NUMBER ||
                type == Character.NON_SPACING_MARK ||  type == Character.COMBINING_SPACING_MARK ||
                type == Character.DECIMAL_DIGIT_NUMBER || type == Character.CONNECTOR_PUNCTUATION);
    }

}
