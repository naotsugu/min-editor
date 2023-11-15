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
package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.model.text.LineEnding;
import com.mammb.code.editor.model.text.OffsetPoint;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * StateHandler.
 * @author Naotsugu Kobayashi
 */
public interface StateHandler {

    void addLineEndingChanged(Consumer<LineEndingSymbol> handler);
    void addCharsetChanged(Consumer<Charset> handler);
    void addCaretPointChanged(Consumer<CaretPoint> handler);
    void addSelectionChanged(Consumer<Range> handler);

    record LineEndingSymbol(String symbol) {
        public LineEndingSymbol(LineEnding lineEnding) {
            this(lineEnding.toString());
        }
        public String asString() {
            return symbol;
        }
    }

    record CaretPoint(int rowNumber, int posOnLine, long pos) {
        public String asString() {
            return "%,d : %,d : %,d".formatted(rowNumber, posOnLine, pos);
        }
    }

    record Range(long start, long end) {
        public Range(OffsetPoint start, OffsetPoint end) {
            this(start.offset(), end.offset());
        }
        public long length() {
            return Math.abs(end - start);
        }
        public String asString() {
            return (length() > 0) ? "%,d chars ".formatted(length()) : "";
        }
    }

}
