package com.mammb.code.editor.ui.app.control;

import javafx.scene.Scene;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CssRun {

    static final CssRun empty = new CssRun("");

    private String cssText;

    public CssRun(String cssText) {
        this.cssText = cssText;
    }

    public CssRun join(CssRun other) {
        return new CssRun(String.join(" ", cssText, other.cssText));
    }

    public void into(Scene scene) {
        scene.getStylesheets().add("data:text/css;base64," +
            Base64.getEncoder().encodeToString(cssText.getBytes(StandardCharsets.UTF_8)));
    }

}
