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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.ui.model.ImeRun;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

/**
 * ImePallet.
 * @author Naotsugu Kobayashi
 */
public interface ImePallet {

    void on(OffsetPoint point);

    void off();

    boolean enabled();

    void composed(TextBuffer<Textual> buffer, List<ImeRun> runs);

    void drawCompose(GraphicsContext gc, TextRun run, double top, double height, double left);

}
