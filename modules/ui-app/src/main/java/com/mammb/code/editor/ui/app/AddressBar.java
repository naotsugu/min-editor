package com.mammb.code.editor.ui.app;

import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class AddressBar extends StackPane {

    private HBox hBox = new HBox();

    private TextField addressText = new TextField();


    public AddressBar() {
        addressText.setFocusTraversable(false);
        hBox.getChildren().add(addressText);
        getChildren().add(hBox);
        setBaseColor(Color.BLACK);
        HBox.setHgrow(addressText, Priority.ALWAYS);
    }


    public void setBaseColor(Color color) {
        addressText.setBackground(new Background(
            new BackgroundFill(color.getBrightness() > 0.5 ? Color.WHITE : Color.BLACK, null, null)));
        addressText.setStyle("-fx-text-fill: " + (color.getBrightness() > 0.5 ? "black" : "white"));
    }

}
