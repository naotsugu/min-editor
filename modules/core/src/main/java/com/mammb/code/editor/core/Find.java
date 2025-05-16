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
 * Represents a search interface for managing and executing find operations.
 * @author Naotsugu Kobayashi
 */
public interface Find {

    /**
     * Finds and returns all matching {@link PointLen} instances that satisfy the given specification.
     * @param spec the specification that defines the criteria for matching {@link PointLen} instances
     * @return a list of {@link PointLen} instances that match the provided specification
     */
    List<PointLen> all(Spec spec);

    /**
     * Finds and returns the next matching {@link PointLen} instance based on the given base point and specification.
     *
     * @param base the starting point from which to search for the next matching instance
     * @param spec the specification defining the criteria for matching {@link PointLen} instances
     * @return an {@link Optional} containing the next matching {@link PointLen} instance if found, or an empty {@link Optional} if none match
     */
    Optional<PointLen> nextOne(Point base, Spec spec);

    /**
     * Finds and returns the previous matching {@link PointLen} instance based on the given base point and specification.
     *
     * @param base the starting point from which to search for the previous matching instance
     * @param spec the specification defining the criteria for matching {@link PointLen} instances
     * @return an {@link Optional} containing the previous matching {@link PointLen} instance if found,
     *         or an empty {@link Optional} if no match is found
     */
    Optional<PointLen> prevOne(Point base, Spec spec);

    /**
     * Finds and returns a list of {@link PointLen} instances based on the current state or criteria.
     *
     * @return a list of {@link PointLen} instances, or an empty list if no matches are found
     */
    List<PointLen> founds();

    /**
     * Finds and returns the next {@link PointLen} instance relative to the specified base point.
     *
     * @param base the starting point from which to search for the next {@link PointLen} instance
     * @return an {@link Optional} containing the next {@link PointLen} instance if found,
     *         or an empty {@link Optional} if no such instance exists
     */
    Optional<PointLen> next(Point base);

    /**
     * Finds and returns the previous matching {@link PointLen} instance
     * relative to the specified base point.
     *
     * @param base the starting point from which to search for the previous {@link PointLen} instance
     * @return an {@link Optional} containing the previous {@link PointLen} instance if found,
     *         or an empty {@link Optional} if no such instance exists
     */
    Optional<PointLen> prev(Point base);

    /**
     * Clears the current state or data of the object, resetting it to its initial state.
     */
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
     * Creates a new {@link Spec} using the given pattern and assigns it a pattern type
     * based on the case sensitivity flag.
     *
     * @param pattern the pattern string to be used for the {@link Spec}
     * @param caseSensitive a flag to indicate if the pattern is case-sensitive;
     *                      if false, the pattern type will be case-insensitive
     * @return a new {@link Spec} initialized with the given pattern and corresponding pattern type
     */
    static Spec specOf(String pattern, boolean caseSensitive) {
        return new Spec(pattern, caseSensitive ? PatternType.LITERAL : PatternType.CASE_INSENSITIVE);
    }

    /**
     * Creates a new {@link Spec} using the given regular expression pattern and assigns it
     * a pattern type of {@link PatternType#REGEX}.
     *
     * @param regex the regular expression pattern to use for the created {@link Spec}
     * @return a new {@link Spec} initialized with the given regular expression and a pattern type of {@link PatternType#REGEX}
     */
    static Spec regexSpecOf(String regex) {
        return new Spec(regex, PatternType.REGEX);
    }

    /**
     * Creates and returns an empty {@link Spec} instance.
     * @return an instance of {@link Spec} with an empty pattern and {@link PatternType#EMPTY} as its type
     */
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
