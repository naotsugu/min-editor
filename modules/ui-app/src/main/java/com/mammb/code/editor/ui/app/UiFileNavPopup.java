package com.mammb.code.editor.ui.app;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import java.nio.file.Path;
import java.util.List;

public class UiFileNavPopup extends Popup {

    /** The theme color. */
    private final UiColor uiColor;

    private final VBox vbox;

    private List<Path> list;

    public UiFileNavPopup(UiColor themeColor, List<Path> list) {

        this.uiColor = themeColor;
        this.list = list;

        vbox = new VBox(8);
        vbox.setBackground(uiColor.backgroundFill());
        for (Path p : list) {
            var label = new Label(p.toString());
            label.setBackground(uiColor.backgroundFill());
            label.setTextFill(uiColor.foreground());
            vbox.getChildren().add(label);
            if (vbox.getChildren().size() > 10) break;
        }
        getContent().add(vbox);
    }

    public void show(Window parent, Point2D point) {
        show(parent);
    }

}
