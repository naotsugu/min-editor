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
package com.mammb.code.editor.fx;

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

    abstract void focus();

    abstract boolean needsCloseConfirmation();

    abstract boolean canClose();

    abstract Optional<Session> close(boolean force);

    abstract void setCloseListener(Consumer<ContentPane> closeListener);

    abstract ReadOnlyObjectProperty<Name> nameProperty();

    abstract boolean externalChanged();

}
