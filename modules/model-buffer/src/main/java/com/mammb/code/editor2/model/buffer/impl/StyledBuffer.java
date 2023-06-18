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

import com.mammb.code.editor2.model.buffer.Scroll;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;

import java.util.List;

/**
 * StyledBuffer.
 * @author Naotsugu Kobayashi
 */
public class StyledBuffer implements TextBuffer<StyledText> {

    /** The peer slice. */
    private final TextBuffer<Textual> buffer;

    /** The styling translate. */
    private final Translate<Textual, StyledText> translator;

    /** The text rows. */
    private List<StyledText> texts;


    /**
     * Constructor.
     * @param buffer the peer slice
     * @param stylingTranslator the styling translate
     */
    public StyledBuffer(
            TextBuffer<Textual> buffer,
            Translate<Textual, StyledText> stylingTranslator) {
        this.buffer = buffer;
        this.translator = stylingTranslator;
    }


    @Override
    public List<StyledText> texts() {
        pullRows();
        return texts;
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
    public Scroll scroll() {
        return buffer.scroll();
    }


    private void pullRows() {
        texts = buffer.texts().stream()
            .map(translator::applyTo)
            .toList();
    }

}
