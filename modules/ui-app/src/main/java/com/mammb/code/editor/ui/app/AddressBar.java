package com.mammb.code.editor.ui.app;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class AddressBar extends StackPane {

    private HBox hBox = new HBox();

    private TextField addressText = new TextField();
    private Button forward = new Button("→");
    private Button back = new Button("←");


    public AddressBar() {
        forward.setFocusTraversable(false);
        back.setFocusTraversable(false);
        addressText.setFocusTraversable(false);
        hBox.getChildren().addAll(back, forward, addressText);
        getChildren().add(hBox);
        setBaseColor(Color.BLACK, addressText, forward, back);
        HBox.setHgrow(addressText, Priority.ALWAYS);
    }


    public void setBaseColor(Color color, Region... regions) {
        String background = (color.getBrightness() > 0.5 ? "white;" : "black;");
        String foreground = (color.getBrightness() <= 0.5 ? "white;" : "black;");
        for (Region region : regions) {
            region.setStyle(
                "-fx-font: 13px \"Consolas\";" +
                " -fx-background-color:" + background +
                " -fx-border-color:" + background +
                " -fx-text-fill: " + foreground);
        }
    }

}
