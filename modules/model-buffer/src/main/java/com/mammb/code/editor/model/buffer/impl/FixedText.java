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
package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.piecetable.buffer.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FixedText.
 * @author Naotsugu Kobayashi
 */
public class FixedText implements TextBuffer<Textual> {

    private final List<Textual> list = new ArrayList<>();

    private int rowIndex = 0;

    private int maxRowSize = 10;

    private int rowCapacity = 3_000;

    private Metrics metrics;


    public FixedText(Path path, int maxRowSize) {
        this.maxRowSize = maxRowSize;
        final Charset cs;
        try (var is = Files.newInputStream(path)) {
            cs = Charsets.charsetOf(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var r = Files.newBufferedReader(path, cs)) {
            int offset = 0;
            int cpOffset = 0;
            for (int row = 0; row < rowCapacity; row++) {
                String text = readLine(r);
                if (text == null) break;
                var p = OffsetPoint.of(row, offset, cpOffset);
                offset += text.length();
                cpOffset += Character.codePointCount(text, 0, text.length());
                list.add(Textual.of(p, text));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Textual> texts() {
        return list.subList(rowIndex, Math.min(rowIndex + maxRowSize, list.size()));
    }

    @Override
    public int maxLineSize() {
        return maxRowSize;
    }

    @Override
    public void setMaxLineSize(int maxSize) {
        maxRowSize = maxSize;
    }

    @Override
    public void push(Edit edit) {
        // do nothing
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public Edit undo() {
        return Edit.empty;
    }

    @Override
    public Edit redo() {
        return Edit.empty;
    }

    @Override
    public List<Textual> prev(int n) {
        if (rowIndex == 0) {
            return Collections.emptyList();
        }
        n = (rowIndex - n) >= 0 ? n : rowIndex;
        List<Textual> delta = list.subList(rowIndex - n, rowIndex);
        rowIndex -= n;
        return delta;
    }

    @Override
    public List<Textual> next(int n) {
        if (rowIndex + 1 >= list.size()) {
            return Collections.emptyList();
        }

        n = (rowIndex + n) < list.size() ? n : list.size() - rowIndex + n;
        List<Textual> delta = list.subList(
            Math.min(rowIndex + maxRowSize, list.size()),
            Math.min(rowIndex + maxRowSize + n, list.size()));
        rowIndex += n;
        return delta;
    }

    @Override
    public Textual subText(OffsetPoint point, int length) {
        final StringBuilder sb = new StringBuilder();
        for (Textual textual : list) {
            if (textual.tailOffset() > point.offset() && textual.offset() < (point.offset() + length)) {
                sb.append(textual.text(),
                    Math.max(point.offset()          - textual.offset(), 0),
                    Math.min(point.offset() + length - textual.offset(), textual.length()));
            }
        }
        return Textual.of(point, sb.toString());
    }

    @Override
    public void save() {
        // do nothing
    }

    @Override
    public void saveAs(Path path) {
        // do nothing
    }

    @Override
    public boolean readOnly() {
        return true;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }


    /**
     * Read line with line ending.
     * @param reader the reader
     * @return the line
     * @throws IOException
     */
    static String readLine(Reader reader) throws IOException {

        if (!reader.markSupported()) {
            reader = new BufferedReader(reader);
        }

        final StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            if (Character.isHighSurrogate((char) ch)) {
                sb.appendCodePoint(Character.toCodePoint((char) ch, (char) reader.read()));
                continue;
            } else {
                sb.append((char) ch);
            }
            if (ch == '\n') {
                break;
            } else if (ch == '\r') {
                reader.mark(1);
                ch = reader.read();
                if (ch == '\n') {
                    sb.append((char) ch);
                } else {
                    reader.reset();
                }
                break;
            }
        }
        if (sb.isEmpty()) {
            return null;
        }
        return sb.toString();
    }

}
