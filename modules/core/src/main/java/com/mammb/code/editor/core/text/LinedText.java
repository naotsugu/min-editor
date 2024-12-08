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
package com.mammb.code.editor.core.text;

/**
 * The lined text.
 * @author Naotsugu Kobayashi
 */
public interface LinedText extends Text {

    /**
     * Get the number of line.
     * @return the number of line
     */
    int line();

    /**
     * Create a new {@link LinedText}.
     * @param line the number of line
     * @param text the text
     * @return a new {@link LinedText}
     */
    static LinedText of(int line, Text text) {
        record LinedTextRecord(int line, Text peer) implements LinedText {
            @Override public int row() { return peer.row(); }
            @Override public String value() { return peer.value(); }
            @Override public double[] advances() { return peer.advances(); }
            @Override public double width() { return peer.width(); }
            @Override public double height() { return peer.height(); }
            @Override public int line() { return line; }
        }
        return new LinedTextRecord(line, text);
    }

}
