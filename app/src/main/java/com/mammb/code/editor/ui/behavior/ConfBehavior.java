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
package com.mammb.code.editor.ui.behavior;

import com.mammb.code.editor.ui.Pointing;
import com.mammb.code.editor.ui.RowsPanel;
import com.mammb.code.editor.ui.TextFlow;
import java.util.Objects;

/**
 * ConfBehavior.
 * @author Naotsugu Kobayashi
 */
public class ConfBehavior {

    /** The text flow pane. */
    private final TextFlow textFlow;

    /** The pointing. */
    private final Pointing pointing;

    /** The rows panel. */
    private final RowsPanel rowsPanel;


    /**
     * Constructor.
     * @param textFlow the text flow pane
     * @param pointing the pointing
     * @param rowsPanel the rows panel
     */
    public ConfBehavior(TextFlow textFlow, Pointing pointing, RowsPanel rowsPanel) {
        this.textFlow = Objects.requireNonNull(textFlow);
        this.pointing = Objects.requireNonNull(pointing);
        this.rowsPanel = Objects.requireNonNull(rowsPanel);
    }


    /**
     * Toggle wrap of text.
     */
    public void toggleTextWrap() {
        textFlow.toggleTextWrap();
        int caretOffset = pointing.caretOffset();
        pointing.clear();
        pointing.addOffset(caretOffset);
        rowsPanel.redraw();
    }

}
