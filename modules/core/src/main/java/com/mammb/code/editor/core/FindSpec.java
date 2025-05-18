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
package com.mammb.code.editor.core;

import java.util.Objects;

/**
 * The find specification.
 * @param pattern the pattern string
 * @param patternType the pattern type
 * @author Naotsugu Kobayashi
 */
public record FindSpec(String pattern, PatternType patternType) {

    /** The pattern type. */
    public enum PatternType { CASE_INSENSITIVE, CASE_SENSITIVE, REGEXP, EMPTY }

    /** The empty find spec. */
    public static final FindSpec EMPTY = new FindSpec("", PatternType.EMPTY);

    public FindSpec {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(patternType);
    }

    /**
     * Creates a new instance of {@link FindSpec} based on the given pattern
     * and case sensitivity flag.
     *
     * @param pattern the pattern string to be used for finding
     * @param caseSensitive flag indicating whether the pattern matching should be case-sensitive
     * @return a new {@link FindSpec} instance with the specified pattern and case sensitivity
     */
    static FindSpec of(String pattern, boolean caseSensitive) {
        return new FindSpec(pattern, caseSensitive ? PatternType.CASE_SENSITIVE : PatternType.CASE_INSENSITIVE);
    }

    /**
     * Creates a new {@link FindSpec} instance for a regular expression pattern.
     *
     * @param regex the regular expression pattern string to be used for finding
     * @return a new {@link FindSpec} instance configured with the specified regular expression pattern
     */
    static FindSpec regexpOf(String regex) {
        return new FindSpec(regex, PatternType.REGEXP);
    }

    /**
     * Checks if the current find specification is empty.
     * The find specification is considered empty if the pattern type is
     * {@code PatternType.EMPTY} or if the pattern string is an empty string.
     *
     * @return {@code true} if the find specification is empty, otherwise {@code false}.
     */
    public boolean isEmpty() {
        return patternType == PatternType.EMPTY || pattern.isEmpty();
    }

}
