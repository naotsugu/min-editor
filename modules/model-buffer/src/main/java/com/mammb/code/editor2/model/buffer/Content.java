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
package com.mammb.code.editor2.model.buffer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Content.
 * @author Naotsugu Kobayashi
 */
public interface Content {

    /**
     * Get the bytes.
     * <pre>
     *     | a | b | c | d |
     *       L startPos
     *                   L  until [d] appears [a, b, c]
     * </pre>
     * @param cpOffset start position(code point index), inclusive
     * @param until the until predicate, exclusive
     * @return the bytes
     */
    byte[] bytes(int cpOffset, Predicate<byte[]> until);

    /**
     * Get the bytes.
     * <pre>
     *     | a | b | c | d | a |
     *                       L startPos
     *      L  until [a] appears [a, b, c]
     * </pre>
     * @param cpOffset start position(code point index), exclusive
     * @param until the until predicate, exclusive
     * @return the bytes
     */
    byte[] bytesBefore(int cpOffset, Predicate<byte[]> until);

    /**
     * Get the charset.
     * @return the charset.
     */
    default Charset charset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Save this content as current path.
     */
    void save();

    /**
     * Save as the specified path.
     * @param path the specified path
     */
    void saveAs(Path path);

}