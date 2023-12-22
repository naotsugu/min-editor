package com.mammb.code.editor.ui.app.control;

import javafx.scene.shape.SVGPath;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

public class StIcon extends SVGPath {
    public static final Css css = st -> CSS."""
    .svg-icon {
      -fx-background-color:\{st.base};
    }
    """;
}
