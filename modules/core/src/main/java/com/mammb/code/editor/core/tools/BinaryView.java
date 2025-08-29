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
package com.mammb.code.editor.core.tools;

import java.util.Iterator;

/**
 * A utility class for formatting binary data into a hexadecimal and character view.
 * @author Naotsugu Kobayashi
 */
public class BinaryView {

    /**
     * Processes the given Source16 object to generate an iterable representation
     * of its binary content formatted in a hexadecimal and character view. Each
     * line includes the offset, hexadecimal representation of bytes, and their
     * corresponding printable ASCII characters (non-printable characters are
     * replaced with a dot '.').
     * @param source the source object containing binary data to be processed. The
     *               source is divided into segments of 16 bytes each, with any
     *               remaining bytes handled accordingly in the final segment.
     * @return an iterable of CharSequence, where each element represents a line of
     *         the formatted output.
     */
    public static Iterable<? extends CharSequence> run(Source16 source) {
        Iterator<CharSequence> iterator = new Iterator<>() {
            int index = 0;
            final StringBuilder sb = new StringBuilder();
            @Override
            public boolean hasNext() {
                boolean ret = index < source.size();
                if (!ret) { source.close();}
                return ret;
            }

            @Override
            public CharSequence next() {

                byte[] bytes = source.get(index);

                sb.setLength(0);
                sb.append(String.format("%08X: ", index * 16));

                for (int i = 0; i < 16; i++) {
                    if (i < bytes.length) {
                        sb.append(String.format("%02X", bytes[i]));
                    } else {
                        sb.append("  ");
                    }
                    if (i % 2 == 1) {
                        sb.append(" ");
                    }
                }
                sb.append(" | ");

                for (int i = 0; i < bytes.length; i++) {
                    char c = (char) bytes[i];
                    if (c >= 32 && c < 127) {
                        sb.append(c);
                    } else {
                        sb.append('.');
                    }
                }
                sb.append("\n");
                index++;
                return sb.toString();
            }
        };
        return () -> iterator;
    }

}
