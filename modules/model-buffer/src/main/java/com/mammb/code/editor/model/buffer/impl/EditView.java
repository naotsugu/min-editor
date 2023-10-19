package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditQueue;
import com.mammb.code.editor.model.slice.TextualSlice;
import com.mammb.code.editor.model.text.Textual;

import java.util.List;
import java.util.stream.Collectors;

public class EditView implements TextualSlice<Textual> {

    private final TextualSlice<Textual> slice;
    /** The edit queue ref. */
    private final EditQueue editQueueRef;

    public EditView(
            TextualSlice<Textual> slice,
            EditQueue editQueue) {
        this.slice = slice;
        this.editQueueRef = editQueue;
    }

    @Override
    public List<Textual> texts() {

        Edit edit = editQueueRef.peek();
        if (edit == null) {
            return slice.texts();
        }

        return slice.texts().stream()
            .map(edit::applyTo)
            .collect(Collectors.toList());

    }


    @Override
    public int pageSize() {
        return slice.pageSize();
    }

    @Override
    public void setPageSize(int pageSize) {
        slice.setPageSize(pageSize);
    }

    @Override
    public List<Textual> prev(int n) {
        editQueueRef.flush();
        return slice.prev(n);
    }

    @Override
    public List<Textual> next(int n) {
        editQueueRef.flush();
        return slice.next(n);
    }

    @Override
    public boolean move(int rowDelta) {
        editQueueRef.flush();
        return slice.move(rowDelta);
    }

    @Override
    public void refresh(int rowNumber) {
        slice.refresh(rowNumber);
    }
}
