package com.mammb.code.editor2.model.edit.impl;

import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditTo;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

public record BackspaceEdit(
        OffsetPoint point,
        String text,
        long occurredOn) implements Edit {

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public Edit flip() {
        return null;
    }

    @Override
    public void apply(EditTo editTo) {

    }

    @Override
    public boolean canMerge(Edit other) {
        return false;
    }


    @Override
    public Edit merge(Edit other) {
        return null;
    }

    @Override
    public boolean acrossRows() {
        return text.indexOf('\n') > -1;
    }

    @Override
    public Textual applyTo(Textual input) {
        return null;
    }
}
