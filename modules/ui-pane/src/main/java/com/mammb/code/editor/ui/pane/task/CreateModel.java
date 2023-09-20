package com.mammb.code.editor.ui.pane.task;

import com.mammb.code.editor.syntax.Syntax;
import com.mammb.code.editor.ui.control.HScrollBar;
import com.mammb.code.editor.ui.control.VScrollBar;
import com.mammb.code.editor.ui.pane.EditorModel;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.concurrent.Task;

import java.nio.file.Path;

public class CreateModel extends Task<Integer> {

    /** The Context. */
    private final Context context;
    /** The Path. */
    private final Path path;
    /** The vertical scroll bar for line scroll. */
    private final VScrollBar vScrollBar;
    /** The horizontal scroll bar for line scroll. */
    private final HScrollBar hScrollBar;
    /** The editor model. */
    private EditorModel model;


    public CreateModel(Context context, Path path, VScrollBar vScrollBar, HScrollBar hScrollBar) {
        this.context = context;
        this.path = path;
        this.vScrollBar = vScrollBar;
        this.hScrollBar = hScrollBar;
    }


    @Override
    protected Integer call() throws Exception {
        model = new EditorModel(
            context,
            context.regionWidth(),
            context.regionHeight(),
            path,
            Syntax.of(path, context.preference().fgColor()),
            vScrollBar, hScrollBar);
        return null;
    }

    public EditorModel getModel() {
        return model;
    }

}
