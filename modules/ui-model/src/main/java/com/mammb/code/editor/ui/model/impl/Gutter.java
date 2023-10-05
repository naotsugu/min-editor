package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.layout.TextRun;
import javafx.scene.canvas.GraphicsContext;

public interface Gutter {

    void draw(GraphicsContext gc, TextRun run, double top, double lineHeight);

    /**
     * Get the width of gutter.
     * @return the width of gutter
     */
    double width();

    boolean checkWidthChanged();

}
