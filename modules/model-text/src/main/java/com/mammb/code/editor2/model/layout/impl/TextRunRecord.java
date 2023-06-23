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
package com.mammb.code.editor2.model.layout.impl;

import com.mammb.code.editor2.model.layout.Layout;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextRun;
import java.util.function.Function;

/**
 * TextRunRecord.
 * @param layout the layout
 * @param text the text
 * @param source the source span
 * @param offsetToX the offset to x function
 * @author Naotsugu Kobayashi
 */
public record TextRunRecord(Layout layout, String text, Span source, Function<Integer, Float> offsetToX) implements TextRun {
}
