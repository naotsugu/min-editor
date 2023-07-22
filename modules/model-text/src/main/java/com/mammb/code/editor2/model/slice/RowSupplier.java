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
package com.mammb.code.editor2.model.slice;

import com.mammb.code.editor2.model.slice.impl.StringText;

/**
 * RowSupplier.
 * @author Naotsugu Kobayashi
 */
public interface RowSupplier {

    /**
     * Get the row string at the specified codepoint offset.
     * <pre>
     *  |a|b|😀|c|\n|
     *  |d|e|😀|f|\n|
     *  |g|h|😀|i|
     *
     *   text.at(-1) -> null
     *   text.at(0)  -> |a|b|😀|c|\n|
     *   text.at(5)  -> |d|e|😀|f|\n|
     *   text.at(10) -> |g|h|😀|i|
     *   text.at(14) -> null
     * </pre>
     * @param cpOffset the codepoint offset
     * @return the row string, non-null
     */
    String at(int cpOffset);


    /**
     * Get the row string before the specified codepoint offset.
     * <pre>
     *  |a|b|😀|c|\n|
     *  |d|e|😀|f|\n|
     *  |g|h|😀|i|
     *
     *   text.before(0)  -> null
     *   text.before(5)  -> |a|b|😀|c|\n|
     *   text.before(10) -> |d|e|😀|f|\n|
     *   text.before(14) -> |g|h|😀|i|
     *   text.before(15) -> null
     * </pre>
     * @param cpOffset the codepoint offset
     * @return the row string, non-null
     */
    String before(int cpOffset);


    /**
     * Create a new RowSupplier from the specified text.
     * @param text the string.
     * @return the created RowSupplier
     */
    static RowSupplier stringOf(String text) {
        return new StringText(text);
    }

}
