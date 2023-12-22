package com.mammb.code.editor.ui.app;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ThemeCss {

    private String base = "#26252D";
    private String text = "#CACACE";
    private String back = "#1A1A1F";
    private String accent = "#3A587F";

    private String hover = "#42424A";
    private String focused = "";
    private String disabled = base;


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
          -fx-base:\{base};
          -fx-accent:\{accent};
          -fx-background:-fx-base;
          -fx-control-inner-background:\{back};
          -fx-control-inner-background-alt: derive(-fx-control-inner-background,-2%);
          -fx-focus-color: -fx-accent;
          -fx-faint-focus-color:\{accent}22;
          -fx-light-text-color:\{text};
          -fx-mark-color: -fx-light-text-color;
          -fx-mark-highlight-color: derive(-fx-mark-color,20%);
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
