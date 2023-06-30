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

import com.mammb.code.editor.javafx.layout.FxLayoutBuilder;
import com.mammb.code.editor.javafx.layout.SpanTranslate;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.LayoutWrapTranslate;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import java.util.LinkedList;
import java.util.List;

/**
 * WrapTextList.
 * @author Naotsugu Kobayashi
 */
public class WrapTextList implements TextList {

    private final TextBuffer<Textual> editBuffer;
    private final Translate<Textual, List<TextLine>> translator = translator();
    private final List<TextLine> lines = new LinkedList<>();
    private int lineOffset = 0;

    public WrapTextList(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
    }

    @Override
    public List<TextLine> lines() {
        return null;
    }

    @Override
    public int prev(int n) {
        return 0;
    }

    @Override
    public int next(int n) {
        return 0;
    }

    @Override
    public int at(int n) {
        return 0;
    }

    private static Translate<Textual, List<TextLine>> translator() {
        FxLayoutBuilder layout = new FxLayoutBuilder();
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutWrapTranslate.of(layout));
    }


    private static void removeHeadRow(List<TextLine> lines, int n) {
        removeRow(lines, n, true);
    }

    private static void removeTailRow(List<TextLine> lines, int n) {
        removeRow(lines, n, false);
    }

    private static void removeRow(List<TextLine> lines, int n, boolean asc) {

        if (n <= 0) return;

        if (n >= lines.size()) {
            lines.clear();
            return;
        }

        int prev = -1;
        while (true) {
            int index = asc ? 0 : lines.size() - 1;
            TextLine line = lines.get(index);
            if (prev != -1 && prev != line.point().row()) {
                n--;
            }
            if (n == 0) break;
            prev = line.point().row();
            lines.remove(index);
        }
    }

}
