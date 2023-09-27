package com.mammb.code.editor.ui.pane.impl;

import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.ui.pane.ScreenText;

import java.nio.file.Path;
import java.util.List;

public class FixedScreenText implements ScreenText {


    public FixedScreenText(Path path, int maxRowSize) {
    }

    @Override
    public List<TextLine> lines() {
        return null;
    }

    @Override
    public void markDirty() {

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
    public boolean scrollAt(int row, int offset) {
        return false;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public TextLine head() {
        return null;
    }
}
