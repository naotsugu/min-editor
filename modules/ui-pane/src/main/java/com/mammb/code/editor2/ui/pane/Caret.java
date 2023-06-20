package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor.javafx.layout.FxLayoutBuilder;
import com.mammb.code.editor2.model.text.OffsetPoint;

public class Caret {

    private OffsetPoint point = OffsetPoint.zero;

    /** The logical caret position x. */
    private double logicalX = 0;

    private final FxLayoutBuilder layout;

    public Caret(FxLayoutBuilder layout) {
        this.layout = layout;
    }


}
