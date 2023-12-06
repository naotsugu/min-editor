package com.mammb.code.editor.ui.app;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class AddressBar extends StackPane {

    private final ThemeColor themeColor;
    private HBox hBox = new HBox();

    private TextField addressText;
    private final Button forward;
    private final Button back;

    public AddressBar(ThemeColor themeColor) {
        this.themeColor = themeColor;
        this.addressText = new ThemeTextField(themeColor);
        this.forward = new ThemeButton("→", themeColor);
        this.back = new ThemeButton("←", themeColor);

        forward.setFocusTraversable(false);
        back.setFocusTraversable(false);
        addressText.setFocusTraversable(false);
        hBox.getChildren().addAll(back, forward, addressText);
        getChildren().add(hBox);
        HBox.setHgrow(addressText, Priority.ALWAYS);
        setBackground(themeColor.backgroundFill());
    }

    StringProperty addressTextProperty() {
        return addressText.textProperty();
    }

}
