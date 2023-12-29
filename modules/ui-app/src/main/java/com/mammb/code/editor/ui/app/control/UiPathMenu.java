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
package com.mammb.code.editor.ui.app.control;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The path menu.
 * @author Naotsugu Kobayashi
 */
public class UiPathMenu extends ContextMenu {

    /** logger. */
    private static final System.Logger log = System.getLogger(UiPathMenu.class.getName());

    /** The parent path. */
    private Path parent;

    /** The path selected handler. */
    private final Consumer<Path> consumer;


    /**
     * Constructor.
     * @param parent the parent path
     * @param paths the path item
     * @param consumer the path selected handler
     */
    public UiPathMenu(Path parent, List<PathItem> paths, Consumer<Path> consumer) {
        super(createItems(paths, consumer));
        this.parent = Objects.requireNonNull(parent);
        this.consumer = Objects.requireNonNull(consumer);
        setAutoHide(true);
        setAutoFix(false);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    }


    /**
     * The key pressed handler
     * @param e the key event
     */
    private void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
            e.consume();
            hide();
        }
        if (e.getCode() == KeyCode.BACK_SPACE) {
            e.consume();
            var p = parent.getParent();
            if (p != null) {
                getItems().clear();
                getItems().addAll(createItems(AddressPath.of(p).listItem(), consumer));
            }
        }
    }


    /**
     * Get the specifies coordinate of the popup anchor point on the screen.
     * @return the specifies coordinate of the popup anchor point on the screen
     */
    public Point2D getAnchor() {
        return new Point2D(getAnchorX(), getAnchorX());
    }


    /**
     * Create the menu item list.
     * @param paths the paths
     * @param consumer the consumer
     * @return the menu item list
     */
    private static MenuItem[] createItems(List<PathItem> paths, Consumer<Path> consumer) {
        return paths.stream().map(p -> {
            var item = new MenuItem(p.name(), UiIcon.contentOf(p.raw()));
            if (Files.isReadable(p.raw())) {
                item.setOnAction(e -> {
                    e.consume();
                    consumer.accept(p);
                });
            } else {
                item.setDisable(true);
            }
            return item;
        }).toArray(MenuItem[]::new);
    }


    /**
     * Get the parent path.
     * @return the parent path
     */
    public Path getParent() {
        return parent;
    }

}
