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
package com.mammb.code.editor.model.find;

/**
 * The Match.
 * @author Naotsugu Kobayashi
 */
public interface Match {

    /**
     * Get the matched offset.
     * @return the matched offset
     */
    int start();

    /**
     * Get the match result string length
     * @return the match result string length
     */
    int length();

    /**
     * Get the matched end offset.
     * @return the matched end offset
     */
    default int end() {
        return start() + length();
    }

    /**
     * Create a new match.
     * @param start the matched offset
     * @param length the match result string length
     * @return a new match
     */
    static Match of(int start, int length) {
        if (start < 0 || length <= 0) {
            throw new IllegalArgumentException();
        }
        record MatchRecord(int start, int length) implements Match { }
        return new MatchRecord(start, length);
    }

}
