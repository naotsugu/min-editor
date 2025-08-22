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

import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Session;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.StackPane;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The ContentPane.
 * @author Naotsugu Kobayashi
 */
public abstract class ContentPane extends StackPane {

    /**
     * Focus the pane.
     */
    abstract void focus();

    /**
     * Check whether the pane needs close confirmation.
     * @return {@code true}, if the pane needs close confirmation
     */
    abstract boolean needsCloseConfirmation();

    /**
     * Check whether the pane can be closed.
     * @return {@code true}, if the pane can be closed
     */
    abstract boolean canClose();

    /**
     * Close the pane.
     * @param force {@code true}, if the pane should be closed even if it needs close confirmation
     * @return the session if closed, otherwise {@code Optional.empty()}
     */
    abstract Optional<Session> close(boolean force);

    /**
     * Set the close listener.
     * @param closeListener the close listener
     */
    abstract void setCloseListener(Consumer<ContentPane> closeListener);

    /**
     * Get the name property.
     * @return the name property
     */
    abstract ReadOnlyObjectProperty<Name> nameProperty();

    /**
     * Check whether the pane is external changed.
     * @return {@code true}, if the pane is external changed
     */
    abstract boolean externalChanged();

}
