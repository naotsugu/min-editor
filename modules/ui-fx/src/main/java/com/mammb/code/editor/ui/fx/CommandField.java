/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.fx;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * The CommandField.
 * @author Naotsugu Kobayashi
 */
public class CommandField extends HBox {

    /** The command label. */
    private final Label commandLabel;

    /** Auto Complete TextField */
    private final TextField textField;

    public CommandField(Node parent) {
        this.commandLabel = new Label();
        commandLabel.setVisible(false);
        commandLabel.setStyle("""
            -fx-font-size: 0.95em;
            -fx-border-style: solid;
            -fx-border-color: gray;
            -fx-border-radius: 3;
            """);

        this.textField = new TextField();

        getChildren().addAll(Icons.terminal(), new Label(" "), commandLabel, textField);
        HBox.setHgrow(textField, Priority.ALWAYS);

    }
}
