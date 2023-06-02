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
package com.mammb.code.editor2.model.buffer.impl;

import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.core.Translate;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.style.StyledText;

import java.util.ArrayList;
import java.util.List;

/**
 * StyledTextBuffer.
 * @author Naotsugu Kobayashi
 */
public class StyledTextBuffer implements TextBuffer<StyledText> {

    /** The pear slice. */
    private final TextBuffer<PointText> buffer;

    /** The styling translate. */
    private final Translate<PointText, StyledText> translator;

    /** The text rows. */
    private List<StyledText> texts = new ArrayList<>();


    public StyledTextBuffer(
            TextBuffer<PointText> buffer,
            Translate<PointText, StyledText> stylingTranslator) {
        this.buffer = buffer;
        this.translator = stylingTranslator;
    }


    @Override
    public List<StyledText> texts() {
        return texts;
    }


    @Override
    public void push(Edit edit) {
        buffer.push(edit);
    }


    private void pullRows() {
        texts = buffer.texts().stream()
            .map(translator::applyTo)
            .toList();
    }

}
