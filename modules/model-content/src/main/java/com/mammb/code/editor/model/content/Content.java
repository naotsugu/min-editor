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
package com.mammb.code.editor.model.content;

import com.mammb.code.editor.model.content.impl.PtContent;
import com.mammb.code.editor.model.content.impl.PtContentMirror;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.piecetable.buffer.Charsets;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    byte[] bytes(long cpOffset, Predicate<byte[]> until);


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
    byte[] bytesBefore(long cpOffset, Predicate<byte[]> until);


    /**
     * Get the code point offset.
     * @param startCpOffset the start position
     * @param until the until
     * @return the code point offset
     */
    long offset(long startCpOffset, Predicate<byte[]> until);


    /**
     * Get the code point offset.
     * @param startCpOffset the start position
     * @param until the until
     * @return the code point offset
     */
    long offsetBefore(long startCpOffset, Predicate<byte[]> until);


    /**
     * Inserts the char sequence into this {@code PieceTable}.
     * @param point the offset point
     * @param cs a char sequence
     */
    void insert(OffsetPoint point, CharSequence cs);


    /**
     * Removes the characters in a substring of this {@code PieceTable}.
     * @param point the beginning index, inclusive
     * @param len the length to be deleted
     */
    void delete(OffsetPoint point, int len);


    /**
     * Get the charset of (original) content.
     * @return the charset of (original) content.
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


    /**
     * Get the content path.
     * @return the content path
     */
    Path path();


    /**
     * Get the length of characters(code points) this content.
     * @return the length of characters(code points)
     */
    long length();

    /**
     * Create a new Content
     * @param path the content path
     * @param traverse the bytes traverse at initial loading
     * @return a created Content
     */
    static Content of(Path path, Consumer<byte[]> traverse) {

        if (path == null) {
            return new PtContent();
        }

        Charset cs = estimateCharset(path);

        Content content = cs.equals(StandardCharsets.UTF_8)
            ? new PtContent(path, traverse)
            : new PtContentMirror(path, cs, traverse);

        return content;
    }


    /**
     * Estimate the charset.
     * @param path the source path
     * @return the charset of estimated
     */
    static Charset estimateCharset(Path path) {
        try (var is = Files.newInputStream(path)) {
            return Charsets.charsetOf(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
