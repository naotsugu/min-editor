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

import com.mammb.code.editor2.model.buffer.impl.Until;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;
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
     *      L  until [a] appears [a, b, c, d]
     * </pre>
     * @param cpOffset start position(code point index), exclusive
     * @param until the until predicate, exclusive
     * @return the bytes
     */
    byte[] bytesBefore(int cpOffset, Predicate<byte[]> until);


    default void traverseRow(Consumer<byte[]> rowConsumer) {
        Predicate<byte[]> lfInclusive = Until.lfInclusive();
        int cpOffset = 0;
        for (;;) {
            // utf8 bytes
            byte[] bytes = bytes(cpOffset, lfInclusive);
            if (bytes.length == 0) break;

            rowConsumer.accept(bytes);

            for (int i = 0; i < bytes.length;) {
                if      ((bytes[i] & 0x80) == 0x00) i += 1;
                else if ((bytes[i] & 0xE0) == 0xC0) i += 2;
                else if ((bytes[i] & 0xF0) == 0xE0) i += 3;
                else if ((bytes[i] & 0xF8) == 0xF0) i += 4;
                else i += 1;
                cpOffset++;
            }
        }
    }

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
