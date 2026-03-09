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

import javafx.scene.Node;
import java.nio.file.Path;

/**
 * The TabContainer.
 * @author Naotsugu Kobayashi
 */
public interface TabContainer {

    /**
     * Selects an existing tab identified by the provided path.
     * @param path the path that identifies the tab to be selected
     * @return true if a tab matching the path is found and selected; false otherwise
     */
    boolean selectExistingTab(Path path);

    /**
     * Adds the specified ContentPane to the container in the next available position.
     * @param pane the ContentPane to be added to the container
     */
    void addNext(ContentPane pane);

    /**
     * Adds the specified ContentPane to the right of the current TabContainer layout.
     * @param pane the ContentPane to be added to the right position in the container
     */
    void addRightPane(ContentPane pane);

    /**
     * Adds the specified ContentPane to the right of the current TabContainer layout
     * and sets focus on it.
     * @param pane the ContentPane to be added and given focus in the right position of the container
     */
    void addRightPaneWithFocus(ContentPane pane);

    void close(ContentPane pane);

    /**
     * Searches for the nearest {@code TabContainer} instance in the hierarchy,
     * starting from the given {@code Node} and traversing up through its parents.
     * If no {@code TabContainer} is found, an empty instance is returned.
     * @param node the starting {@code Node} from which the search begins
     * @return the nearest {@code TabContainer} instance if found; otherwise, an empty {@code TabContainer} instance
     */
    static TabContainer find(Node node) {
        for (;;) {
            if (node == null) return EMPTY;
            if (node instanceof TabContainer tabContainer) return tabContainer;
            node = node.getParent();
        }
    }

    /**
     * An empty {@code TabContainer} instance.
     */
    TabContainer EMPTY = new TabContainer() {
        @Override public boolean selectExistingTab(Path path) { return false; }
        @Override public void addNext(ContentPane pane) { }
        @Override public void addRightPane(ContentPane pane) { }
        @Override public void addRightPaneWithFocus(ContentPane pane) { }
        @Override public void close(ContentPane pane) { }
    };

}
