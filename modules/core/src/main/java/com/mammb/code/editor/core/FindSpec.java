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
     * Create a new {@link FindSpec}.
     * @param pattern the pattern string
     * @param caseSensitive case-sensitive?
     * @return a new {@link FindSpec}
     */
    static FindSpec of(String pattern, boolean caseSensitive) {
        return new FindSpec(pattern, caseSensitive ? PatternType.CASE_SENSITIVE : PatternType.CASE_INSENSITIVE);
    }

    /**
     * Create a new {@link FindSpec}.
     * @param regex the regex
     * @return a new {@link FindSpec}
     */
    static FindSpec regexpOf(String regex) {
        return new FindSpec(regex, PatternType.REGEXP);
    }

    /**
     * Get whether this spec is empty or not.
     * @return {@code true}, if this spec is empty
     */
    public boolean isEmpty() {
        return patternType == PatternType.EMPTY || pattern.isEmpty();
    }

}
