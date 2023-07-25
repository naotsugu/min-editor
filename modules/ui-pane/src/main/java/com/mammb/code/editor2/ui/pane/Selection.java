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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import javafx.scene.canvas.GraphicsContext;

/**
 * Selection.
 * @author Naotsugu Kobayashi
 */
public interface Selection {

    void start(OffsetPoint offset);

    void clear();

    OffsetPoint startOffset();

    OffsetPoint endOffset();

    boolean started();

    void to(OffsetPoint toOffset);

    void draw(GraphicsContext gc, TextRun run, double offsetY, double sideBearing);

    default int length() {
        return max().offset() - min().offset();
    }

    default OffsetPoint min() {
        return (startOffset().offset() <= endOffset().offset()) ? startOffset() : endOffset();
    }

    default OffsetPoint max() {
        return (startOffset().offset() <= endOffset().offset()) ? endOffset() : startOffset();
    }

}
