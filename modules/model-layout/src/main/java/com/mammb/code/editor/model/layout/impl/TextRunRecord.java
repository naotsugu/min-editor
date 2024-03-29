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
package com.mammb.code.editor.model.layout.impl;

import com.mammb.code.editor.model.layout.Layout;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.layout.Span;

import java.util.function.Function;

/**
 * TextRunRecord.
 * @param layout the layout
 * @param y the position y
 * @param start the start char index at the source span
 * @param length the char length of this text run
 * @param textLine the textLine to which this run belongs
 * @param source the source span
 * @param offsetToX the offset to x function
 * @param xToOffset the x to offset function
 * @author Naotsugu Kobayashi
 */
public record TextRunRecord(
    Layout layout,
    double y,
    int start,
    int length,
    TextLine textLine,
    Span source,
    Function<Integer, Float> offsetToX,
    Function<Double, Integer> xToOffset) implements TextRun {
}
