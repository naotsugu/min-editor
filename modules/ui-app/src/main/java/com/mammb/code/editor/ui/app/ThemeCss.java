package com.mammb.code.editor.ui.app;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ThemeCss {

    private String base = "#26252D";
    private String hover = "#42424A";
    private String focused = "";
    private String disabled = base;

    public String text = "#CACACE";

    String dataUrl() {
        return "data:text/css;base64," +
            Base64.getEncoder().encodeToString(css().getBytes(StandardCharsets.UTF_8));
    }

    String css() {
        return String.join("", root(), button(), dialog());
    }


    String root() {
        return STR."""
        .root {
          -fx-accent:rgba(121,134,203,0.5); /* Hue.INDIGO */
        }
        """;
    }

    String button() {
        return STR."""
        .button {
          -fx-background-color:\{base};
          -fx-text-fill:\{text};
        }
        .button:hover, .button:focused {
          -fx-background-color:\{base};
        }
        """;
    }

    String dialog() {
        return STR."""
        .dialog-pane {
          -fx-background-color:\{base};
        }
        .dialog-pane > .content {
          -fx-text-fill:\{text};
        }
        """;
    }
}
