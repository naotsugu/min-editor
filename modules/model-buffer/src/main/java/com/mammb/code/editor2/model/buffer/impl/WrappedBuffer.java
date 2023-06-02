package com.mammb.code.editor2.model.buffer.impl;

import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.edit.Edit;

import java.util.List;

public class WrappedBuffer<T extends PointText> implements TextBuffer<T> {

    @Override
    public List<T> texts() {
        return null;
    }

    @Override
    public void push(Edit edit) {

    }
}
