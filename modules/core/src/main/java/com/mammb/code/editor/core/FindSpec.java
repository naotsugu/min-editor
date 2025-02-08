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
 * @author Naotsugu Kobayashi
 */
public record FindSpec(String pattern, PatternType patternType) {

    public enum PatternType { CASE_INSENSITIVE, CASE_SENSITIVE, REGEXP, EMPTY }

    public static final FindSpec EMPTY = new FindSpec("", PatternType.EMPTY);

    public FindSpec {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(patternType);
    }

    static FindSpec of(String pattern) {
        return new FindSpec(pattern, PatternType.CASE_INSENSITIVE);
    }

    static FindSpec of(String pattern, boolean caseSensitive) {
        return new FindSpec(pattern, caseSensitive ? PatternType.CASE_SENSITIVE : PatternType.CASE_INSENSITIVE);
    }

    static FindSpec regexpOf(String pattern) {
        return new FindSpec(pattern, PatternType.REGEXP);
    }

    static FindSpec caseSensOf(String pattern) {
        return new FindSpec(pattern, PatternType.CASE_SENSITIVE);
    }

    public boolean isEmpty() {
        return patternType == PatternType.EMPTY || pattern.isEmpty();
    }
}
