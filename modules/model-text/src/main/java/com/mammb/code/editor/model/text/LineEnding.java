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
package com.mammb.code.editor.model.text;

/**
 * The LineEnding.
 * @author Naotsugu Kobayashi
 */
public enum LineEnding {

    /** line feed. */
    LF,
    /** carriage return. */
    CR,
    /** windows line separator. */
    CRLF;


    /**
     * Get whether CRLF or not.
     * @return {@code true}, if CRLF
     */
    public boolean isCrLf() {
        return this == CRLF;
    }


    /**
     * Get whether LF or not.
     * @return {@code true}, if LF
     */
    public boolean isLf() {
        return this == LF;
    }


    /**
     * Get the char array.
     * @return the char array
     */
    public char[] cs() {
        return str().toCharArray();
    }


    /**
     * Get the first char.
     * @return the first char
     */
    public char c() {
        if (this == CRLF) throw new IllegalStateException(this.toString());
        return str().toCharArray()[0];
    }


    /**
     * Get the line ending string.
     * @return the line ending string
     */
    public String str() {
        return switch (this) {
            case LF -> "\n";
            case CR -> "\r";
            case CRLF -> "\r\n";
        };
    }


    /**
     * Gets whether the specified char array matches a newline character.
     * @param x the specified char array
     * @return {@code true}, if the specified char array matches a newline character
     */
    public boolean match(char... x) {
        if (x == null || x.length == 0) return false;
        return switch (this) {
            case LF, CR -> x.length == 1 && x[0] == c();
            case CRLF -> x.length == 2 && x[0] == '\r' && x[1] == '\n';
        };
    }


    /**
     * Gets whether the specified byte matches a newline character.
     * @param b the specified byte
     * @return {@code true}, if the specified byte matches a newline character
     */
    public boolean match(byte b) {
        return match((char) b);
    }


    /**
     * Unify line ending.
     * @param string unify string
     * @return the unified string
     */
    public String unify(String string) {
        return switch (this) {
            case LF -> unifyLf(string);
            case CR -> string; // not supported
            case CRLF -> unifyCrLf(string);
        };
    }


    /**
     * Get the platform line separator.
     * @return the platform line separator
     */
    public static LineEnding platform() {
        var s = System.getProperty("line.separator");
        return LF.str().equals(s) ? LF
             : CR.str().equals(s) ? CR
             : CRLF.str().equals(s) ? CRLF
             : LF;
    }


    /**
     * Unify line breaks in the given string to LF line breaks.
     * @param string the given string
     * @return the unified string
     */
    private static String unifyLf(String string) {
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
     * <pre>
     *
     * </pre>
     * @param string the given string
     * @return the unified string
     */
    private static String unifyCrLf(String string) {
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
     * Count the number of the specified char in the specified char sequence.
     * @param cs the char sequence
     * @param ch the specified char
     * @return the number of the specified char
     */
    private static int count(CharSequence cs, char ch) {
        return (cs == null) ? 0 : (int) cs.chars().filter(c -> c == ch).count();
    }

}
