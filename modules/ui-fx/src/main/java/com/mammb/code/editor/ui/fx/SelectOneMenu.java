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

import com.mammb.code.editor.core.Theme;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * The select one menu.
 * @author Naotsugu Kobayashi
 */
public class SelectOneMenu extends FxContextMenu {

    private SelectOneMenu(boolean modal, MenuItem... menuItems) {
        super(modal, menuItems);
        setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            """);
        setAvailableHeight(400);
    }

    public SelectOneMenu() {
        this(false);
    }

    public static <E> SelectOneMenu of(Collection<E> list, Consumer<E> consumer) {
        Color textColor = Color.web(Theme.current.fgColor().web());
        return new SelectOneMenu(true, list.stream()
            .map(e -> {
                var label = new Text(e.toString());
                label.setFill(textColor);
                label.setStyle("-fx-font: 15px \"Consolas\";");
                CustomMenuItem item = new CustomMenuItem(label, true);
                item.setOnAction(a -> {
                    a.consume();
                    consumer.accept(e);
                });
                return item;
            }).toArray(CustomMenuItem[]::new));
    }

}
