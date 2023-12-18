package com.mammb.code.editor.ui.app;

import javafx.scene.control.Dialog;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.lang.StringTemplate.STR;

public class UiDialog<R> extends Dialog<R> {

    /** The theme color. */
    private final UiColor uiColor;

    public UiDialog(UiColor themeColor) {
        super();
        uiColor = themeColor;
        getDialogPane().getStylesheets().add(css(uiColor));
    }

    private String css(UiColor tc) {
        var css = STR."""
            .dialog-pane {
              -fx-background-color:\{tc.background()};
            }
            .dialog-pane > .content {
                -fx-text-fill:\{tc.foreground()};
            }
            .button {
                -fx-background-color:\{tc.background()};
                -fx-text-fill:\{tc.foreground()};
            }
            .button:hover, .button:focused {
                -fx-background-color:\{tc.backgroundActive()};
            }
            """;
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }
}
