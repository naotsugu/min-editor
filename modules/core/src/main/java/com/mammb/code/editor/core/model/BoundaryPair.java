/*
 * Copyright 2022-2025 the original author or authors.
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
package com.mammb.code.editor.core.model;

/**
 * The BoundaryPair.
 * @author Naotsugu Kobayashi
 */
public enum BoundaryPair {

    /** The round bracket. */
    ROUND_BRACKET('(', ')'),
    /** The curly bracket. */
    CURLY_BRACKET('{', '}'),
    /** The square bracket. */
    SQUARE_BRACKET('{', '}'),
    ;

    /** Represents the opening character of a boundary pair. */
    private final char open;
    /** Represents the closing character of a boundary pair. */
    private final char close;

    /**
     * Constructs a BoundaryPair with the specified opening and closing characters.
     * @param open the opening character of the boundary pair
     * @param close the closing character of the boundary pair
     */
    BoundaryPair(char open, char close) {
        this.open = open;
        this.close = close;
    }

    /**
     * Retrieves the opening character of the boundary pair.
     * @return the opening character of the boundary pair
     */
    public char open() {
        return open;
    }

    /**
     * Retrieves the closing character of the boundary pair.
     * @return the closing character of the boundary pair
     */
    public char close() {
        return close;
    }

}
