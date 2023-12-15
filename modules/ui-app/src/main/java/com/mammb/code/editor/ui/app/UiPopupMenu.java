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

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import java.nio.file.Path;
import java.util.List;

/**
 * The UiPopupMenu.
 * @author Naotsugu Kobayashi
 */
public class UiPopupMenu extends ContextMenu {

    /** The theme color. */
    private final UiColor uiColor;


    public UiPopupMenu(UiColor themeColor, MenuItem... items) {
        super(items);
        this.uiColor = themeColor;
        set

    }


    public static UiPopupMenu of(UiColor tc, List<Path> paths) {
        var items = paths.stream()
            .map(p -> UiMenuItem.of(tc, p)).toArray(MenuItem[]::new);
        return new UiPopupMenu(tc, items);
    }

}
