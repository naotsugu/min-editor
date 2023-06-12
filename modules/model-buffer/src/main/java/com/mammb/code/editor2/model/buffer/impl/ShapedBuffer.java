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
import com.mammb.code.editor2.model.layout.LayoutBuilder;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;

import java.util.Collection;
import java.util.List;

public class ShapedBuffer implements TextBuffer<TextLine> {

    /** The pear slice. */
    private final TextBuffer<StyledText> buffer;

    /** The styling translate. */
    private final Translate<StyledText, Span> translator;

    private final LayoutBuilder layoutBuilder;

    private List<TextLine> lines;


    public ShapedBuffer(
            TextBuffer<StyledText> buffer,
            LayoutBuilder layoutBuilder,
            Translate<StyledText, Span> translator) {
        this.buffer = buffer;
        this.layoutBuilder = layoutBuilder;
        this.translator = translator;
    }


    @Override
    public List<TextLine> texts() {
        if (lines == null) {
            pullLines();
        }
        return lines;
    }


    @Override
    public void push(Edit edit) {
        buffer.push(edit);
    }


    private void pullLines() {
        List<Span> spans = buffer.texts().stream()
            .map(StyledText::spans)
            .flatMap(Collection::stream)
            .map(translator::applyTo)
            .toList();
        layoutBuilder.add(spans);
        lines = layoutBuilder.layout();
    }

}
