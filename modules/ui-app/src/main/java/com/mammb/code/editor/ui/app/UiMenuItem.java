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
import javafx.scene.control.MenuItem;
import java.nio.file.Path;

/**
 * The UiMenuItem.
 * @author Naotsugu Kobayashi
 */
public class UiMenuItem extends MenuItem {

    /** The theme color. */
    private final UiColor uiColor;


    public UiMenuItem(UiColor themeColor, String text, Node graphic) {
        super(text, graphic);
        this.uiColor = themeColor;
        setStyle(css(uiColor));
    }


    public static UiMenuItem of(UiColor themeColor, Path path) {
        return new UiMenuItem(themeColor,
            path.getFileName().toString(), UiIcon.contentOf(themeColor, path));
    }


    private static String css(UiColor tc) {
        return """
            -fx-text-fill: %1$s;
            -fx-font: 14px \"Consolas\";
            -fx-background-color: transparent;
            """.formatted(
                tc.foregroundColorString());
    }

}