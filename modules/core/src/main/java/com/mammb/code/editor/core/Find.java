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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.mammb.code.editor.core.Point.*;

/**
 * The find.
 * @author Naotsugu Kobayashi
 */
public interface Find {

    List<PointLen> all(Spec spec);
    Optional<PointLen> nextOne(Point base, Spec spec);
    Optional<PointLen> prevOne(Point base, Spec spec);
    List<PointLen> founds();
    Optional<PointLen> next(Point base);
    Optional<PointLen> prev(Point base);
    void clear();

    /**
     * The empty find.
     * @return the empty find
     */
    static Find empty() {
        return new Find() {
            @Override public List<PointLen> all(Spec spec) { return List.of(); }
            @Override public Optional<PointLen> nextOne(Point base, Spec spec) { return Optional.empty(); }
            @Override public Optional<PointLen> prevOne(Point base, Spec spec) { return Optional.empty(); }
            @Override public List<PointLen> founds() { return List.of(); }
            @Override public Optional<PointLen> next(Point base) { return Optional.empty(); }
            @Override public Optional<PointLen> prev(Point base) { return Optional.empty(); }
            @Override public void clear() { }
        };
    }

    /**
     * Create a new {@link Spec}.
     * @param pattern the pattern string
     * @param caseSensitive case-sensitive?
     * @return a new {@link Spec}
     */
    static Spec specOf(String pattern, boolean caseSensitive) {
        return new Spec(pattern, caseSensitive ? PatternType.LITERAL : PatternType.CASE_INSENSITIVE);
    }

    /**
     * Create a new {@link Spec}.
     * @param regex the regex
     * @return a new {@link Spec}
     */
    static Spec regexSpecOf(String regex) {
        return new Spec(regex, PatternType.REGEX);
    }

    static Spec emptySpecOf() {
        return new Spec("", PatternType.EMPTY);
    }

    /** pattern type. */
    enum PatternType {
        /** literal. */
        LITERAL,
        /** case-insensitive. */
        CASE_INSENSITIVE,
        /** regex. */
        REGEX,
        /** empty. */
        EMPTY;
    }

    /**
     * The find spec.
     * @param pattern the pattern string
     * @param patternType the pattern type
     */
    record Spec(String pattern, PatternType patternType) {

        /**
         * Constructor.
         * @param pattern the pattern string
         * @param patternType the pattern type
         */
        public Spec {
            Objects.requireNonNull(pattern);
            Objects.requireNonNull(patternType);
        }

        /**
         * Get whether this spec is empty or not.
         * @return {@code true}, if this spec is empty
         */
        public boolean isEmpty() {
            return patternType == PatternType.EMPTY || pattern.isEmpty();
        }

    }

}
