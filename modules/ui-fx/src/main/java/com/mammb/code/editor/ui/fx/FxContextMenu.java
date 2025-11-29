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

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * The FxContextMenu.
 * @author Naotsugu Kobayashi
 */
public class FxContextMenu extends ContextMenu {

    private double availableHeight = 0;

    public FxContextMenu(MenuItem... menuItems) {
        super(menuItems);
        buildEventHandler();
    }

    public FxContextMenu() {
        this(new MenuItem[0]);
    }

    public void setAvailableHeight(double height) {
        availableHeight = height;
    }

    @Override
    public void show(Node anchor, Side side, double dx, double dy) {
        super.show(anchor, side, dx, dy);
        if (availableHeight <= 0) return;
        if (getSkin() != null && getSkin().getNode() instanceof Region region) {
            region.setMaxHeight(availableHeight);
            setY(anchor.localToScreen(anchor.getBoundsInLocal()).getMaxY() + dy);
        }
        focusFirstItem();
    }

    private void focusFirstItem() {
        if (getSkin() == null || getSkin().getNode() == null) {
            return;
        }
        Node node = getSkin().getNode().lookup(".menu-item");
        node.requestFocus();
        node.fireEvent(new KeyEvent(KEY_PRESSED, "", "", DOWN, false, false, false, false));
    }

    private void buildEventHandler() {
        final EventHandler<KeyEvent> blockKeyPressed = keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP, DOWN, ESCAPE -> {}
                case ENTER -> {
                    keyEvent.consume();
                    EventTarget eventTarget = keyEvent.getTarget();
                    if (eventTarget instanceof Node node) {
                        MenuItem menuItem = (MenuItem) node.getProperties().get(MenuItem.class);
                        if (menuItem != null && !menuItem.isDisable() && menuItem.isVisible()) {
                            menuItem.fire();
                        }
                    }
                    Platform.runLater(this::hide);
                }
                default -> keyEvent.consume();
            }
        };
        final EventHandler<KeyEvent> blockKeyTyped = KeyEvent::consume;

        setOnShown(_ -> {
            if (getScene() != null) {
                getScene().addEventFilter(KeyEvent.KEY_PRESSED, blockKeyPressed);
                getScene().addEventFilter(KeyEvent.KEY_TYPED, blockKeyTyped);
            }
        });
        setOnHidden(_ -> {
            if (getScene() != null) {
                getScene().removeEventFilter(KeyEvent.KEY_PRESSED, blockKeyPressed);
                getScene().removeEventFilter(KeyEvent.KEY_TYPED, blockKeyTyped);
            }
        });
    }

}
