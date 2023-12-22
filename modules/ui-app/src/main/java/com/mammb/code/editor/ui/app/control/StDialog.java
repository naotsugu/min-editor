package com.mammb.code.editor.ui.app.control;

import javafx.scene.control.Dialog;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

/**
 * The StDialog.
 * @author Naotsugu Kobayashi
 */
public class StDialog <R> extends Dialog<R> {

    public static final Css css = st -> CSS."""
    .dialog-pane {
      -fx-background-color:\{st.base};
    }
    .dialog-pane > .content {
      -fx-text-fill:\{st.text};
    }
    .button {
      -fx-background-color:\{st.base};
      -fx-text-fill:\{st.text};
    }
    .button:hover, .button:focused {
      -fx-background-color:\{st.base};
    }
    """;

}
