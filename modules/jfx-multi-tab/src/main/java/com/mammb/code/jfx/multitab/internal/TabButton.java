/*
 * Copyright 2026- the original author or authors.
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
package com.mammb.code.jfx.multitab.internal;

import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;

class TabButton extends javafx.scene.control.Tab {

    private final Label label = new Label();

    public TabButton() {

        setGraphic(label);
        setClosable(false);
        getStyleClass().add("add-tab-button");
        setStyle("-fx-background-color: transparent;");

        label.setGraphic(plus());
        label.getStyleClass().add("add-tab-button-label");
    }

    public static void install(TabPane tabPane, Supplier<Tab> tabSupplier) {
        TabButton button = new TabButton();
        tabPane.getTabs().addLast(button);
        button.label.setOnMouseClicked(_ -> {
            Tab newNormalTab = tabSupplier.get();
            int addTabIndex = tabPane.getTabs().indexOf(button);
            tabPane.getTabs().add(addTabIndex, newNormalTab);
            tabPane.getSelectionModel().select(newNormalTab);
        });

        tabPane.getSelectionModel().selectedItemProperty()
            .addListener((_, old, selected) -> {
                if (selected == button) {
                    if (old != null && tabPane.getTabs().contains(old)) {
                        tabPane.getSelectionModel().select(old);
                    } else {
                        int index = Math.max(0, tabPane.getTabs().size() - 2);
                        tabPane.getSelectionModel().select(index);
                    }
                }
            });

        tabPane.getStylesheets().add(String.join(",", "data:text/css;base64",
            Base64.getEncoder().encodeToString("""
            /* hide '+' button from overflow menu */
            .context-menu .radio-menu-item:last-child {
              -fx-min-height: 0px;
              -fx-pref-height: 0px;
              -fx-max-height: 0px;
              -fx-padding: 0px;
              -fx-opacity: 0%;
            }
            .add-tab-button .tab-label {
                -fx-text-fill: transparent;
            }
            .add-tab-button-label {
              -fx-padding: 2 5 2 5;
              -fx-background-radius: 15px;
              -fx-border-radius: 15px;
            }
            .add-tab-button-label:hover {
              -fx-background-color: rgba(128, 128, 128, 0.5);
            }
            """.getBytes(StandardCharsets.UTF_8))));
    }


    /**
     * Get the plus icon.
     * @return the plus icon
     */
    private static SVGPath plus() {
        var svg = new SVGPath();
        svg.setContent("""
            M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4
            """);
        svg.getStyleClass().add("icon");
        svg.setStroke(Color.GRAY);
        return svg;
    }

}
