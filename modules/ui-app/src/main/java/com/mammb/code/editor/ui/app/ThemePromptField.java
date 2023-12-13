/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.editor.ui.app;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.util.function.Consumer;

/**
 * The ThemeCommandField.
 * @author Naotsugu Kobayashi
 */
public class ThemePromptField extends StackPane {

    /** The theme color. */
    private final ThemeColor themeColor;

    /** The text field. */
    private final ThemeTextField text;

    /** The prompt. */
    private final Group prompt;

    /** The border. */
    private final Border border;

    /** The border active. */
    private final Border borderActive;


    /**
     * Constructor.
     * @param tc the theme color
     */
    public ThemePromptField(ThemeColor tc) {

        themeColor = tc;
        text = new ThemeTextField(tc);
        prompt = new Group();
        border = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT));
        borderActive = new Border(new BorderStroke(themeColor.foreground(), BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT));

        var background = new Background(new BackgroundFill(themeColor.background().darker(), new CornerRadii(4), Insets.EMPTY));
        setBackground(background);
        text.setBackground(background);
        setBorder(border);

        setPrompt(ThemeIcon.contentOf(themeColor, ""));
        setFocusTraversable(false);

        var hbox = new HBox(prompt, text);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox.setMargin(prompt, new Insets(0, 0, 0, 8));
        getChildren().addAll(hbox);

        initHandler();
    }


    /**
     * Get the text property.
     * @return the text property
     */
    public StringProperty textProperty() {
        return text.textProperty();
    }


    /**
     * Set the prompt icon.
     * @param icon the prompt icon
     */
    public void setPrompt(ThemeIcon icon) {
        if (prompt.getChildren().isEmpty()) {
            prompt.getChildren().add(icon);
        } else {
            prompt.getChildren().set(0, icon);
        }
    }

    public void accentuatePrompt(boolean accentuate) {
        var icon = (ThemeIcon) prompt.getChildren().get(0);
        if (accentuate) {
            icon.fillAccent();
        } else {
            icon.fillForeground();
        }
    }


    public void textCommitted(Consumer<String> consumer) {
        text.setOnKeyTyped(e -> {
            var bytes = e.getCharacter().getBytes();
            if (bytes.length > 0 && bytes[0] == 13) { // enter
                consumer.accept(text.getText());
                e.consume();
            }
        });
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        text.textProperty().addListener((ob, o, n) -> setPrompt(ThemeIcon.contentOf(themeColor, extension(n))));
    }


    private String extension(String string) {
        int index = string.lastIndexOf(".") + 1;
        return (index > 0 && index < string.length()) ? string.substring(index) : "";
    }

}
