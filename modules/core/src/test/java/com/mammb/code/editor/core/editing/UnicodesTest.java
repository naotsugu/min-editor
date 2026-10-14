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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test of {@link Unicodes}.
 * @author Naotsugu Kobayashi
 */
@Nested
@DisplayName("escape")
class UnicodesTest {

    @Test
    void printableAsciiIsKeptAsIs() {
        assertEquals("Hello, World! 123 ~", Unicodes.escape("Hello, World! 123 ~"));
    }

    @Test
    void emptyStringReturnsEmptyString() {
        assertEquals("", Unicodes.escape(""));
    }

    @Test
    void nonAsciiIsEscapedWithUpperCaseHex() {
        assertEquals("\\u3053\\u3093\\u306B\\u3061\\u306F", Unicodes.escape("こんにちは"));
        assertEquals("\\u00E9", Unicodes.escape(String.valueOf((char) 0xE9)));
    }

    @Test
    void supplementaryCharacterBecomesSurrogatePair() {
        String emoji = new String(Character.toChars(0x1F600));
        assertEquals("\\uD83D\\uDE00", Unicodes.escape(emoji));
    }

    @Test
    void backslashIsEscaped() {
        assertEquals("C:\\u005Ctemp", Unicodes.escape("C:\\temp"));
    }

    @Test
    void controlCharactersAreEscaped() {
        assertEquals("a\\u000Ab\\u0009c", Unicodes.escape("a\nb\tc"));
    }

    @Test
    void acceptsAnyCharSequence() {
        assertEquals("\\u3042", Unicodes.escape(new StringBuilder("あ")));
    }

    @ParameterizedTest
    @NullSource
    void nullInput(CharSequence input) {
        assertEquals("", Unicodes.escape(input));
    }

    @Test
    void outputContainsPrintableAsciiOnly() {
        String all = IntStream.rangeClosed(0, 0x2FF)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        assertTrue(Unicodes.escape(all).chars().allMatch(c -> c >= 0x20 && c <= 0x7E));
    }
}

@Nested
@DisplayName("unescape (valid input)")
class UnescapeValid {

    @Test
    void plainTextIsKeptAsIs() {
        assertEquals("Hello, World!", Unicodes.unescape("Hello, World!"));
        assertEquals("", Unicodes.unescape(""));
    }

    @Test
    void decodesBasicSequences() {
        assertEquals("こんにちは", Unicodes.unescape("\\u3053\\u3093\\u306B\\u3061\\u306F"));
    }

    @Test
    void acceptsLowerAndUpperCaseHex() {
        assertEquals("A", Unicodes.unescape("\\u0041"));
        assertEquals(String.valueOf((char) 0xE9), Unicodes.unescape("\\u00e9"));
        assertEquals(String.valueOf((char) 0xE9), Unicodes.unescape("\\u00E9"));
    }

    @Test
    void acceptsMultipleUCharacters() {
        assertEquals("A", Unicodes.unescape("\\uuu0041"));
    }

    @Test
    void surrogatePairIsCombinedIntoOneCodePoint() {
        String result = Unicodes.unescape("\\uD83D\\uDE00");
        assertEquals(1, result.codePointCount(0, result.length()));
        assertEquals(0x1F600, result.codePointAt(0));
    }

    @Test
    void mixesEscapedAndLiteralText() {
        assertEquals("a-A-b", Unicodes.unescape("a-\\u0041-b"));
    }

    @Test
    void otherBackslashSequencesAreNotTouched() {
        assertEquals("line\\nbreak", Unicodes.unescape("line\\nbreak"));
    }

    @Test
    void loneTrailingBackslashIsKept() {
        assertEquals("abc\\", Unicodes.unescape("abc\\"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "plain ASCII",
        "こんにちは, world!",
        "C:\\temp\\users",
        "tab\there\nnewline",
        "emoji \uD83D\uDE00 end"
    })
    void roundTripRestoresOriginal(String original) {
        assertEquals(original, Unicodes.unescape(Unicodes.escape(original)));
    }

    @ParameterizedTest
    @NullSource
    void nullInputThrowsNpe(String input) {
        assertThrows(NullPointerException.class, () -> Unicodes.unescape(input));
    }

    @Test
    void nullPolicyThrowsNpe() {
        assertThrows(NullPointerException.class, () -> Unicodes.unescape("abc", null));
    }
}

@Nested
@DisplayName("unescape (malformed input)")
class UnescapeMalformed {

    // Each of these is incomplete or contains a non-hex digit.
    private static final String[] MALFORMED = {
        "\\u",
        "\\u0",
        "\\u004",
        "\\uuu",
        "\\u00zz",
        "\\u00G1",
        "\\u-041",
        "\\u 041"
    };

    @ParameterizedTest
    @ValueSource(strings = {
        "\\u", "\\u0", "\\u004", "\\uuu", "\\u00zz", "\\u00G1", "\\u-041", "\\u 041"
    })
    void throwPolicyRaisesIllegalArgumentException(String input) {
        assertThrows(IllegalArgumentException.class,
            () -> Unicodes.unescape(input, Unicodes.MalformedPolicy.THROW));
    }

    @Test
    void defaultOverloadBehavesLikeThrowPolicy() {
        assertThrows(IllegalArgumentException.class, () -> Unicodes.unescape("\\u00zz"));
    }

    @Test
    void exceptionMessageContainsIndexOfBrokenSequence() {
        var e = assertThrows(IllegalArgumentException.class,
            () -> Unicodes.unescape("ok: \\u00zz", Unicodes.MalformedPolicy.THROW));
        assertTrue(e.getMessage().contains("index 4"), e.getMessage());
    }

    @Test
    void ignorePolicyLeavesMalformedSequencesUntouched() {
        for (String input : MALFORMED) {
            assertEquals(input, Unicodes.unescape(input, Unicodes.MalformedPolicy.IGNORE));
        }
    }

    @Test
    void ignorePolicyStillDecodesValidSequencesAroundBrokenOnes() {
        assertEquals("A\\u00zzB",
            Unicodes.unescape("\\u0041\\u00zz\\u0042", Unicodes.MalformedPolicy.IGNORE));
    }

    @Test
    void ignorePolicyHandlesIncompleteSequenceAtEnd() {
        assertEquals("A\\u12",
            Unicodes.unescape("\\u0041\\u12", Unicodes.MalformedPolicy.IGNORE));
    }

    @Test
    void ignorePolicyDoesNotAffectValidInput() {
        assertEquals("こんにちは",
            Unicodes.unescape("\\u3053\\u3093\\u306B\\u3061\\u306F", Unicodes.MalformedPolicy.IGNORE));
    }
}

