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

import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import java.util.List;

/**
 * TextLineRecord.
 * @param point the OffsetPoint
 * @param lineIndex the line index
 * @param length the char length
 * @param width the width of line
 * @param height the height
 * @param runs the text runs
 * @author Naotsugu Kobayashi
 */
public record TextLineRecord(
    OffsetPoint point,
    int lineIndex,
    int length,
    double width,
    double height,
    List<TextRun> runs) implements TextLine {
}
