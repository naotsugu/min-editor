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
import com.mammb.code.editor2.model.layout.LayoutTranslate;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import java.util.LinkedList;
import java.util.List;

public class Screen {

    private final TextBuffer<Textual> editBuffer;

    private final Translate<Textual, TextLine> translator;
    private final FxLayoutBuilder layout;
    private List<TextLine> lines = new LinkedList<>();

    public Screen(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
        this.layout = new FxLayoutBuilder();
        this.translator = translator();
    }

    public void draw(GraphicsContext gc) {
        double offsetY = 0;
        for (TextLine textLine : lines()) {
            for (TextRun run : textLine.runs()) {
                if (run.style().font() instanceof Font font && !gc.getFont().equals(font)) {
                    gc.setFont(font);
                }
                gc.fillText(run.text(), run.layout().x(), run.layout().y() + offsetY);
            }
            offsetY += textLine.runs().get(0).layout().height();
        }
    }

    public void clear() {
        lines.clear();
    }

    public void up(int n) {
        List<TextLine> list = editBuffer.prev(n).stream()
            .map(translator::applyTo).toList();
        lines.addAll(0, list);
        for (int i = 0; i < list.size(); i++)
            lines.remove(lines.size() - 1);
    }

    public void down(int n) {
        List<TextLine> list = editBuffer.next(n).stream()
            .map(translator::applyTo).toList();
        lines.addAll(list);
        for (int i = 0; i < list.size(); i++)
            lines.remove(0);
    }

    private List<TextLine> lines() {
        if (lines.isEmpty()) {
            List<TextLine> list = editBuffer.texts().stream()
                .map(translator::applyTo).toList();
            lines.addAll(list);
        }
        return lines;
    }

    private Translate<Textual, TextLine> translator() {
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutTranslate.of(layout));
    }

}