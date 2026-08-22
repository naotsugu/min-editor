/*
 * Copyright 2026- the original author or authors.
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
package com.mammb.code.jfx.tabcontainer;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.StackPane;

/**
 * The ContentPane.
 * @author Naotsugu Kobayashi
 */
public abstract class ContentPane extends StackPane {

    /**
     * Focus the pane.
     */
    abstract public void focus();

    /**
     * Check whether this pane can be closed without confirmation.
     * {@code false} if the target is automatically saved when the application exits.
     * @return {@code true}, if this pane can be closed without confirmation
     */
    abstract public boolean canCloseQuiet();

    /**
     * Check whether this pane can be closed exited confirmation.
     * {@code true} if the target is automatically saved when the application exits.
     * @return {@code true}, if this pane can be exited without confirmation
     */
    public boolean canExitQuiet() {
        return canCloseQuiet();
    }

    /**
     * Check whether the pane can be closed.
     * @return {@code true}, if the pane can be closed
     */
    abstract public boolean closeRequest();

    /**
     * Close the pane.
     */
    abstract public void close();

    abstract public String asString();

    abstract public ReadOnlyObjectProperty<String> shortNameProperty();

    abstract public ReadOnlyObjectProperty<String> fullNameProperty();

}
