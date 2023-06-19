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
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;

import java.util.List;
import java.util.Objects;

/**
 * StyledBuffer.
 * @author Naotsugu Kobayashi
 */
public class StyledBuffer implements TextBuffer<StyledText> {

    /** The peer slice. */
    private final TextBuffer<Textual> buffer;

    /** The styling translate. */
    private final Translate<Textual, StyledText> translator;


    /**
     * Constructor.
     * @param buffer the peer slice
     * @param stylingTranslator the styling translate
     */
    public StyledBuffer(
            TextBuffer<Textual> buffer,
            Translate<Textual, StyledText> stylingTranslator) {
        this.buffer = Objects.requireNonNull(buffer);
        this.translator = Objects.requireNonNull(stylingTranslator);
    }

    @Override
    public List<StyledText> texts() {
        return buffer.texts().stream()
            .map(translator::applyTo)
            .toList();
    }

    @Override
    public int maxLineSize() {
        return buffer.maxLineSize();
    }

    @Override
    public void setMaxLineSize(int maxSize) {
        buffer.setMaxLineSize(maxSize);
    }

    @Override
    public void push(Edit edit) {
        buffer.push(edit);
    }

    @Override
    public void prev(int n) {
        buffer.prev(n);
    }

    @Override
    public void next(int n) {
        buffer.next(n);
    }

}
