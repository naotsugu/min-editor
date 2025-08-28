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
package com.mammb.code.editor.core.editing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A utility class for formatting binary data into a hexadecimal and character view.
 * @author Naotsugu Kobayashi
 */
public class Binary {

    private static final int BYTES_PER_LINE = 16;

    /**
     * Formats the content of an InputStream into a hexadecimal and ASCII character view
     * and writes the formatted output to an OutputStream. Each line of the output contains
     * the hexadecimal representation of a fixed number of bytes, along with their equivalent
     * printable ASCII characters. Non-printable characters are represented with a dot ('.').
     * @param in the InputStream to read the binary data from
     * @param out the OutputStream to write the formatted output to
     * @throws IOException if an I/O error occurs during reading or writing
     */
    public static void format(InputStream in, OutputStream out) throws IOException {

        byte[] buffer = new byte[BYTES_PER_LINE];
        int bytesRead;
        long offset = 0;

        var sb = new StringBuilder();
        while ((bytesRead = in.read(buffer)) != -1) {

            sb.append(String.format("%08X: ", offset));

            for (int i = 0; i < BYTES_PER_LINE; i++) {
                if (i < bytesRead) {
                    sb.append(String.format("%02X", buffer[i]));
                } else {
                    sb.append("  ");
                }
                if (i % 2 == 1) {
                    sb.append(" ");
                }
            }
            sb.append(" | ");

            for (int i = 0; i < bytesRead; i++) {
                char c = (char) buffer[i];
                if (c >= 32 && c < 127) {
                    sb.append(c);
                } else {
                    sb.append('.');
                }
            }
            sb.append("\n");

            out.write(sb.toString().getBytes());

            sb.setLength(0);
            offset += bytesRead;
        }

    }

}
