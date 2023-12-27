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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * The PathNavi.
 * @author Naotsugu Kobayashi
 */
public class PathNavi extends ContextMenu {

    /**
     * Constructor.
     */
    public PathNavi(List<Path> paths, Consumer<Path> consumer) {
        super(createItems(paths, consumer));
        setAutoHide(true);
        setAutoFix(false);
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
    private static MenuItem[] createItems(List<Path> paths, Consumer<Path> consumer) {
        return paths.stream().map(p -> {
            var item = new MenuItem(p.getFileName().toString(), Icon.contentOf(p));
            if (Files.isReadable(p)) {
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

}
