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

import com.mammb.code.editor.ui.pane.EditorUpCall;
import com.mammb.code.editor.ui.pane.Session;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import java.nio.file.Path;

/**
 * The AppEditorUpCall.
 * @author Naotsugu Kobayashi
 */
public class AppEditorUpCall implements EditorUpCall {

    private StringProperty addressPathProperty;

    private BooleanProperty modifiedProperty;

    @Override
    public void pathChanged(Path path, Session prev) {
        if (addressPathProperty == null) return;
        addressPathProperty.set((path == null) ? "" : path.toString());
    }

    @Override
    public void contentModified(boolean modified, Path path) {
        if (modifiedProperty == null) return;
        modifiedProperty.set(modified);
    }

    public void setAddressPathProperty(StringProperty addressPathProperty) {
        this.addressPathProperty = addressPathProperty;
    }

    public void setModifiedProperty(BooleanProperty modifiedProperty) {
        this.modifiedProperty = modifiedProperty;
    }

}
