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

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.StringTemplate.STR;

/**
 * The UiPopupMenu.
 * @author Naotsugu Kobayashi
 */
public class UiPopupMenu extends ContextMenu {

    /** The theme color. */
    private final UiColor uiColor;


    /**
     * Constructor.
     * @param themeColor the theme color
     * @param items the menu item
     */
    private UiPopupMenu(UiColor themeColor, MenuItem... items) {
        super(items);
        this.uiColor = themeColor;
        setStyle(css(uiColor));
    }


    public static UiPopupMenu of(UiColor tc, List<Path> paths, Consumer<Path> consumer) {
        var items = paths.stream()
            .map(path -> UiMenuItem.of(tc, path, consumer))
            .toArray(MenuItem[]::new);
        return new UiPopupMenu(tc, items);
    }


    public void show(Node ownerNode, double anchorX, double anchorY) {
        super.show(ownerNode, anchorX, anchorY);
    }


    private String css(UiColor tc) {
        return STR."""
            -fx-background-color:\{tc.background()};
            """;
    }

}
