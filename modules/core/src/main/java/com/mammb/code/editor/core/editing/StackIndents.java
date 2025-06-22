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
package com.mammb.code.editor.core.editing;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The stack indents.
 * @author Naotsugu Kobayashi
 */
class StackIndents {

    /** The indent unit. */
    private static final String INDENT = "    ";

    /**
     * Indentify.
     * @param cs the input char sequence
     * @param s the start character
     * @param e the end character
     * @return the indented text
     */
    static CharSequence indentify(CharSequence cs, char s, char e) {

        Deque<Character> stack = new ArrayDeque<>();
        final StringBuilder sb = new StringBuilder(cs.length());

        for (int i = 0; i < cs.length(); i++) {
            char ch = cs.charAt(i);
            if (ch == '\r') {
                continue;
            }
            if (ch == '\n') {
                ch = ' ';
            }

            if (ch == s) {
                sb.append(ch);
                sb.append('\n');
                stack.push(s);
                i += skipWhitespace(cs, i);
            } else if (ch == e) {
                if (stack.peek() == s) {
                    stack.pop();
                    sb.append('\n');
                    sb.append(INDENT.repeat(stack.size()));
                    sb.append(ch);
                    i += skipWhitespace(cs, i);
                } else {
                    sb.append(ch);
                }
            } else {
                sb.append(ch);
            }

            if (sb.charAt(sb.length() - 1) == '\n') {
                sb.append(INDENT.repeat(stack.size()));
            }
        }
        return sb;
    }

    private static int skipWhitespace(CharSequence cs, int index) {
        for (int i = index; i < cs.length(); i++) {
            char ch = cs.charAt(i);
            if (!Character.isWhitespace(ch)) {
                return i - index;
            }
        }
        return cs.length() - index;
    }

}
