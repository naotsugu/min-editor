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
package com.mammb.code.editor2.model.layout;

import com.mammb.code.editor2.model.layout.impl.TextLineRecord;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TextLine.
 * @author Naotsugu Kobayashi
 */
public interface TextLine extends Textual {

    @Override
    default String text() {
        return runs().stream().map(TextRun::text).collect(Collectors.joining());
    }

    @Override
    OffsetPoint point();

    /**
     * Get the text runs.
     * @return the text runs
     */
    List<TextRun> runs();


    /**
     * Create a new TextLine.
     * @param point the OffsetPoint
     * @param runs the text runs
     * @return a created TextLine
     */
    static TextLine of(OffsetPoint point, List<TextRun> runs) {
        return new TextLineRecord(point, runs);
    }

}
