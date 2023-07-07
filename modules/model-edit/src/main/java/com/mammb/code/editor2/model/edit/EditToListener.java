package com.mammb.code.editor2.model.edit;

public class EditToListener implements EditListener {

    private final EditTo editTo;

    public EditToListener(EditTo editTo) {
        this.editTo = editTo;
    }

    @Override
    public void handle(Edit edit) {
        edit.apply(editTo);
    }

}
