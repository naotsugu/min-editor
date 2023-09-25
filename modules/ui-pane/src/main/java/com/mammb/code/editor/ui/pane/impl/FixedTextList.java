package com.mammb.code.editor.ui.pane.impl;

import com.mammb.code.editor.model.layout.LineLayout;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.ui.pane.TextList;

import java.nio.file.Path;
import java.util.List;

public class FixedTextList implements TextList {


    public FixedTextList(Path path, int maxRowSize) {
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
    public LineLayout lineLayout() {
        return null;
    }

    @Override
    public TextLine head() {
        return null;
    }
}
