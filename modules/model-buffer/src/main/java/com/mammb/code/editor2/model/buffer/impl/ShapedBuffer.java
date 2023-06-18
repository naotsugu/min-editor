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
import com.mammb.code.editor2.model.buffer.Wrap;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.layout.LayoutBuilder;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;

import java.util.Collection;
import java.util.List;

/**
 * ShapedBuffer.
 * @author Naotsugu Kobayashi
 */
public class ShapedBuffer implements TextBuffer<TextLine>, Wrap {

    /** The peer slice. */
    private final TextBuffer<StyledText> buffer;

    /** The styling translate. */
    private final Translate<StyledText, Span> translator;

    /** The layout builder. */
    private final LayoutBuilder layoutBuilder;

    /** The buffer of wrapped lines. */
    private List<TextLine> lines;

    /** The wrap width. */
    private double wrapWidth = 0;

    /** The line offset. */
    private int lineOffset = 0;


    /**
     * Constructor.
     * @param buffer the peer buffer
     * @param layoutBuilder the layout builder
     * @param translator the styling translate
     */
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
        return lines.subList(lineOffset, Math.min(lineOffset + buffer.maxLineSize(), lines.size()));
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

        if (wrapWidth < 0) {
            return buffer.scroll();
        }

        return new Scroll() {

            @Override
            public void prev(int n) {
                if (lineOffset - n >= 0) {
                    lineOffset -= n;
                } else {
                    buffer.scroll().prev(n);
                    pullLines();
                    int scrolledLines = headLinesOfRow(n);
                    lineOffset = lineOffset + scrolledLines - n;
                }
            }

            @Override
            public void next(int n) {
                if (lines.size() > lineOffset + maxLineSize() + n + 1) {
                    lineOffset += n;
                } else {
                    int scrolledLines = headLinesOfRow(n);
                    buffer.scroll().next(n);
                    lineOffset = lineOffset - scrolledLines + n;
                }
            }
        };
    }

    @Override
    public void setWrapWidth(double wrapWidth) {
        if (layoutBuilder.setWrapWidth(wrapWidth)) {
            this.wrapWidth = wrapWidth;
        }
    }

    private void pullLines() {

        List<StyledText> sources = buffer.texts();
        List<Span> spans = sources.stream()
            .map(StyledText::spans)
            .flatMap(Collection::stream)
            .map(translator::applyTo)
            .toList();
        layoutBuilder.add(spans);
        lines = layoutBuilder.layout();

    }

    private int headLinesOfRow(int n) {

        if (n < 1 || lines.isEmpty()) {
            return 0;
        }
        int to = lines.get(0).point().row() + n;
        int count = 0;
        for (var line : lines) {
            if (line.point().row() >= to) break;
            count++;
        }
        return count;
    }

}
