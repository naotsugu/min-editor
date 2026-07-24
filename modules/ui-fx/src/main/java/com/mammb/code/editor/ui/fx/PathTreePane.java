/*
 * Copyright 2023-2025 the original author or authors.
 * <p>
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

import com.mammb.code.jfx.multitab.ContentPane;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.nio.file.Path;

/**
 * The PathTreePane.
 * @author Naotsugu Kobayashi
 */
public class PathTreePane extends ContentPane {

    /** The logger. */
    private static final System.Logger log = System.getLogger(PathTreePane.class.getName());

    /** The pathTreeView. */
    private final PathTreeView pathTreeView;
    /** The short name property. */
    private final SimpleObjectProperty<String> shortNameProperty = new SimpleObjectProperty<>("Files");
    /** The full name property. */
    private final SimpleObjectProperty<String> fullNameProperty = new SimpleObjectProperty<>("Files");

    /**
     * Constructor.
     * @param roots the root path
     */
    public PathTreePane(Path... roots) {
        pathTreeView = new PathTreeView(roots);
        getChildren().add(pathTreeView);
    }

    @Override
    public void focus() {
    }

    @Override
    public boolean canCloseQuiet() {
        return true;
    }

    @Override
    public boolean closeRequest() {
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public String asString() {
        return "";
    }

    @Override
    public ReadOnlyObjectProperty<String> shortNameProperty() {
        return shortNameProperty;
    }

    @Override
    public ReadOnlyObjectProperty<String> fullNameProperty() {
        return fullNameProperty;
    }

}
