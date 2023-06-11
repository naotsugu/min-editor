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
package com.mammb.code.editor2.model.buffer;

import com.mammb.code.editor2.model.buffer.impl.PtContent;
import com.mammb.code.editor2.model.buffer.impl.EditBuffer;
import com.mammb.code.editor2.model.buffer.impl.StyledBuffer;
import com.mammb.code.editor2.model.text.PointText;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.style.StylingTranslate;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Buffers.
 * @author Naotsugu Kobayashi
 */
public final class Buffers {
    private Buffers() { }


    public static TextBuffer<StyledText> of(int maxRowSize) {
        return of(maxRowSize, null);
    }

    public static TextBuffer<StyledText> of(int maxRowSize, Path path) {
        return new StyledBuffer(
            editBuffer(maxRowSize, path),
            StylingTranslate.passThrough());
    }

    private static TextBuffer<PointText> editBuffer(int maxRowSize, Path path) {
        return new EditBuffer(
            Objects.isNull(path) ? new PtContent() : new PtContent(path),
            maxRowSize);
    }

}
