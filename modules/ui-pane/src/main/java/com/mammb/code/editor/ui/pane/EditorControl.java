package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.ui.model.StateHandler;
import javafx.beans.property.StringProperty;

public class EditorControl {

    private StringProperty addressPath;

    public void bind(StateHandler stateHandler) {
        stateHandler.addContentStateChanged(c -> {
            switch (c.type()) {
                case LOAD -> addressPath.set(c.path().toString());
            }
        });
    }

}
