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

import java.util.ArrayList;
import java.util.List;

/**
 * The string utilities.
 * @author Naotsugu Kobayashi
 */
public class Strings {

    /**
     * Get the length of specified character sequences last row.
     * @param cs the specified char sequence
     * @return the length of last row(not code point)
     */
    public static int lengthOfLastRow(CharSequence cs) {
        int index = Math.max(0, Math.min(lastIndexOf(cs, LineEnding.LF.c()) + 1, cs.length()));
        return cs.subSequence(index, cs.length()).length();
    }


    /**
     * Get the index within this string of the last occurrence of the specified character.
     * @param cs the specified char sequence
     * @param ch a character
     * @return the index of the last occurrence of the character in the character sequence
     * represented by this object,or {@code -1} if the character does not occur
     */
    public static int lastIndexOf(CharSequence cs, char ch) {
        for (int i = cs.length() - 1; i >= 0; i--) {
            if (cs.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Get the all indexes of the specified character.
     * @param cs the specified char sequence
     * @param ch a character to be used on checking
     * @return the all indexes of ch occurrence
     */
    public static int[] indexesOf(CharSequence cs, char ch) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < cs.length(); i++) {
            if (cs.charAt(i) == ch) {
                list.add(i);
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }


    /**
     * Count the number of row in the specified char sequence.
     * @param cs the specified char sequence
     * @return the number of row
     */
    public static int countRow(CharSequence cs) {
        return (cs == null || cs.isEmpty()) ? 0
            : count(cs, LineEnding.LF.c()) + (!LineEnding.LF.match(cs.charAt(cs.length() - 1)) ? 1 : 0);
    }


    /**
     * Count the number of line feed in the specified char sequence.
     * @param cs the specified char sequence
     * @return the number of line feed
     */
    public static int countLf(CharSequence cs) {
        return count(cs, LineEnding.LF.c());
    }


    /**
     * Count the number of the specified char in the specified char sequence.
     * @param cs the char sequence
     * @param ch the specified char
     * @return the number of the specified char
     */
    public static int count(CharSequence cs, char ch) {
        return (cs == null) ? 0 : (int) cs.chars().filter(c -> c == ch).count();
    }


    /**
     * Returns the number of Unicode code points in the specified text.
     * @param cs the specified text
     * @return the number of Unicode code points
     */
    public static int codePointCount(CharSequence cs) {
        return (cs == null) ? 0 : codePointCount(cs, 0, cs.length());
    }


    /**
     * Returns the number of Unicode code points in the specified text range of this String.
     * @param cs the specified text
     * @param beginIndex the index to the first char of the text range
     * @param endIndex the index after the last char of the text range
     * @return the number of Unicode code points
     */
    public static int codePointCount(CharSequence cs, int beginIndex, int endIndex) {
        return (cs == null) ? 0 : (int) cs.subSequence(beginIndex, endIndex).codePoints().count();
    }


    /**
     * Get whether the given byte is a carriage return.
     * @param ch the byte to be checked
     * @return {@code true}, if given byte is a carriage return
     */
    public static boolean isLf(char ch) {
        return LineEnding.LF.match(ch);
    }


    /**
     * Get whether the given bytes is line feed and carriage return.
     * @param ch1 the first byte to be checked
     * @param ch2 the second byte to be checked
     * @return {@code true}, if given bytes is a CRLF
     */
    public static boolean isCrLf(char ch1, char ch2) {
        return LineEnding.CRLF.match(ch1, ch2);
    }


    /**
     * Unify line breaks in the given string to LF line breaks.
     * @param string the given string
     * @return the unified string
     */
    public static String unifyLf(String string) {
        if (string == null || string.isEmpty()) return string;
        int len = 0;
        char[] chars = new char[string.length()];
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (LineEnding.CR.match(ch)) {
                if (i + 1 < string.length() && LineEnding.LF.match(string.charAt(i + 1))) {
                    // just skip
                } else {
                    chars[len++] = LineEnding.LF.c();
                }
            } else {
                chars[len++] = ch;
            }
        }
        return new String(chars, 0, len);
    }


    /**
     * Unify line breaks in the given string to CR LF line breaks.
     * @param string the given string
     * @return the unified string
     */
    public static String unifyCrLf(String string) {
        if (string == null || string.isEmpty()) return string;
        int lfCount = count(string, LineEnding.LF.c());
        if (lfCount == 0) {
            return string;
        }
        int len = 0;
        char prev = 0;
        char[] chars = new char[string.length() + lfCount];
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (LineEnding.LF.match(ch) && !LineEnding.CRLF.match(prev, ch)) {
                chars[len++] = LineEnding.CR.c();
            }
            chars[len++] = ch;
            prev = ch;
        }
        return new String(chars, 0, len);
    }


    /**
     * Get the number of bytes from the first byte of UTF-8 when expressed in UTF-16.
     * @param utf8FirstByte the first byte of UTF-8
     * @return the number of bytes when expressed in UTF-16
     */
    public static short lengthByteAsUtf16(byte utf8FirstByte) {
        if ((utf8FirstByte & 0x80) == 0x00) {
            return 1; // BMP
        } else if ((utf8FirstByte & 0xE0) == 0xC0) {
            return 1; // BMP
        } else if ((utf8FirstByte & 0xF0) == 0xE0) {
            return 1; // BMP
        } else if ((utf8FirstByte & 0xF8) == 0xF0) {
            return 2;
        } else {
            return 0;
        }
    }

}
