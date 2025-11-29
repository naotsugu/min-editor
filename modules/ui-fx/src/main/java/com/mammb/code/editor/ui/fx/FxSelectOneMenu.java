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
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Collection;
import java.util.function.Consumer;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * The select one menu.
 * @author Naotsugu Kobayashi
 */
public class FxSelectOneMenu extends ContextMenu {

    private double availableHeight = 400;

    public FxSelectOneMenu() {
        setStyle("""
                -fx-background-color: derive(-fx-control-inner-background,10%);
                """);
    }

    public static <E> FxSelectOneMenu of(Collection<E> list, Consumer<E> consumer) {
        var menu = new FxSelectOneMenu();
        Color textColor = Color.web(Theme.current.fgColor().web());
        list.forEach(e -> {
            var label = new Text(e.toString());
            label.setFill(textColor);
            CustomMenuItem item = new CustomMenuItem(label, true);
            item.setOnAction(_ -> consumer.accept(e));
            menu.getItems().add(item);
        });
        return menu;
    }

    public void setAvailableHeight(double height) {
        availableHeight = height;
    }

    @Override
    public void show(Node anchor, Side side, double dx, double dy) {
        super.show(anchor, side, dx, dy);
        if (getSkin().getNode() instanceof Region region) {
            region.setMaxHeight(availableHeight);
            setY(anchor.localToScreen(anchor.getBoundsInLocal()).getMaxY() + dy);
        }
    }

    void focusFirstItem() {
        if (getSkin() == null) {
            return;
        }
        Node node = getSkin().getNode().lookup(".menu-item");
        node.requestFocus();
        node.fireEvent(new KeyEvent(KEY_PRESSED, "", "", DOWN, false, false, false, false));
    }

}
