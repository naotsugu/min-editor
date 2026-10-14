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

import java.util.HexFormat;
import java.util.Objects;

/**
 * Utility methods for escaping and unescaping Unicode characters
 * using the Java-style {@code \}{@code uXXXX} notation.
 * @author Naotsugu Kobayashi
 */
public class Unicodes {

    private static final HexFormat HEX = HexFormat.of().withUpperCase();

    /**
     * Defines how {@link #unescape(String, MalformedPolicy)} reacts to a
     * malformed {@code \}{@code u} sequence (too short, or containing
     * non-hexadecimal digits).
     */
    public enum MalformedPolicy {
        /** Throw an {@link IllegalArgumentException}. */
        THROW,
        /** Leave the malformed sequence untouched in the output. */
        IGNORE
    }

    private Unicodes() {
    }

    /**
     * Escapes every character outside the printable ASCII range
     * (U+0020 to U+007E), as well as the backslash itself, into
     * {@code \}{@code uXXXX} sequences.
     *
     * <p>Supplementary characters (e.g. emoji) are emitted as surrogate
     * pairs, exactly as the Java compiler would expect them. The backslash
     * is escaped too, so that {@link #unescape(String)} always restores
     * the original string.
     *
     * @param input the text to escape; must not be {@code null}
     * @return the escaped text, containing printable ASCII characters only
     */
    public static String escape(CharSequence input) {
        Objects.requireNonNull(input);
        var sb = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 0x20 && c <= 0x7E && c != '\\') {
                sb.append(c);
            } else {
                sb.append("\\u").append(HEX.toHexDigits(c));
            }
        }
        return sb.toString();
    }

    /**
     * Unescapes the given text.
     * Equivalent to {@code unescape(input, MalformedPolicy.IGNORE)}.
     *
     * @param input the text to unescape; must not be {@code null}
     * @return the unescaped text
     */
    public static String unescape(String input) {
        return unescape(input, MalformedPolicy.IGNORE);
    }

    /**
     * Replaces every {@code \}{@code uXXXX} sequence with the character it
     * represents. Like the Java language specification, this accepts
     * multiple {@code u} characters (e.g. {@code \}{@code uuu0041}) and
     * both upper- and lower-case hex digits. Surrogate pairs written as two
     * consecutive escapes are combined into a single code point naturally.
     *
     * <p>When a sequence is malformed, the behavior depends on the policy:
     * {@link MalformedPolicy#THROW} raises an exception, while
     * {@link MalformedPolicy#IGNORE} copies the backslash as-is and keeps
     * scanning, so the broken sequence stays in the output unchanged.
     *
     * @param input  the text to unescape; must not be {@code null}
     * @param policy how to handle malformed sequences; must not be {@code null}
     * @return the unescaped text
     * @throws IllegalArgumentException if a sequence is malformed and the
     *         policy is {@link MalformedPolicy#THROW}
     */
    public static String unescape(String input, MalformedPolicy policy) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(policy);
        int length = input.length();
        var sb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char c = input.charAt(i);
            if (c == '\\' && i + 1 < length && input.charAt(i + 1) == 'u') {
                // skip all consecutive 'u' characters.
                int digitsStart = i + 1;
                while (digitsStart < length && input.charAt(digitsStart) == 'u') {
                    digitsStart++;
                }
                int digitsEnd = digitsStart + 4;
                int decoded = decodeHex4(input, digitsStart, digitsEnd);
                if (decoded >= 0) {
                    sb.append((char) decoded);
                    i = digitsEnd;
                    continue;
                }
                if (policy == MalformedPolicy.THROW) {
                    throw new IllegalArgumentException(
                        "Malformed Unicode escape at index " + i);
                }
                // IGNORE: fall through and copy the backslash literally.
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    /**
     * Decodes exactly four hex digits, or returns -1 if the range is
     * out of bounds or contains a non-hex character.
     */
    private static int decodeHex4(String s, int from, int to) {
        if (to > s.length()) {
            return -1;
        }
        for (int i = from; i < to; i++) {
            if (!HexFormat.isHexDigit(s.charAt(i))) {
                return -1;
            }
        }
        return HexFormat.fromHexDigits(s, from, to);
    }

    public static void main(String[] args) {
        String original = "こんにちは, world! 😀 C:\\temp";
        String escaped = escape(original);
        System.out.println(escaped);
        System.out.println(unescape(escaped));
        System.out.println(original.equals(unescape(escaped))); // true

        String broken = "ok: \\u0041, broken: \\u00zz, short: \\u12";
        System.out.println(unescape(broken, MalformedPolicy.IGNORE));
        try {
            unescape(broken);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

}
