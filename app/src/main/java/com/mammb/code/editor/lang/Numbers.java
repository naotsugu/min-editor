/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.lang;

import java.util.Arrays;

/**
 * Number utility.
 * @author Naotsugu Kobayashi
 */
public class Numbers {

    /**
     * Get whether the char is a java number part.
     * @param ch the char to check
     * @return {@code true} if the char is a java number part
     */
    public static boolean isJavaNumberPart(char ch) {
        return isNum(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F') ||
               (ch == 'x') || (ch == 'X') ||
               (ch == '.') || (ch == '+') ||  (ch == '-') ||  (ch == '_') ||
               (ch == 'l') || (ch == 'L');
    }


    /**
     * Get whether the String is a valid Java number.
     * @param str the {@link String} to check
     * @return {@code true} if the string is a correctly formatted number
     */
    public static boolean isJavaNumber(final String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        final char[] chars = toCharArrayStripedUnderscore(str);
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        final int start = chars[0] == '-' || chars[0] == '+' ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0' && ! str.contains(".")) { // leading 0, skip if is a decimal number
            if (chars[start + 1] == 'x' || chars[start + 1] == 'X') { // leading 0x/0X
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                        && (chars[i] < 'a' || chars[i] > 'f')
                        && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
            if (Character.isDigit(chars[start + 1])) {
                // leading 0, but not hex, must be octal
                int i = start + 1;
                for (; i < chars.length; i++) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwards
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                && (chars[i] == 'd'
                || chars[i] == 'D'
                || chars[i] == 'f'
                || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }


    /**
     * Get whether the char is a numeric.
     * @param ch the char to check
     * @return {@code true} if the char is a numeric
     */
    private static boolean isNum(char ch) {
        return ch >= '0' && ch <= '9';
    }


    /**
     * Strip the underscore on java number literal.
     * Underscores located in incorrect places will not be removed.
     * @param source the source string
     * @return the striped char array
     */
    static char[] toCharArrayStripedUnderscore(String source) {

        if (source == null || source.isEmpty()) {
            return new char[0];
        }

        int n = 0;
        char[] temp = new char[source.length()];
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (i > 0 && i < source.length() - 1) {
                char prev = source.charAt(i - 1);
                char next = source.charAt(i + 1);
                if (ch == '_' && (isNum(prev) || prev == '_') && (isNum(next) || next == '_')) {
                    continue;
                }
            }
            temp[n++] = ch;
        }
        return Arrays.copyOf(temp, n);
    }

}
