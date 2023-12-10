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

import com.mammb.code.editor.ui.pane.EditorHandle;
import com.mammb.code.editor.ui.pane.EditorHandListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import java.nio.file.Path;

/**
 * The AppEditorHandle.
 * @author Naotsugu Kobayashi
 */
public class AppEditorHandle implements EditorHandle {

    private StringProperty addressPathProperty;
    private BooleanProperty modifiedProperty;

    private EditorHandListener downCallListener;

    @Override
    public void pathChangedUpCall(Path path) {
        if (addressPathProperty == null) return;
        addressPathProperty.set((path == null) ? "" : path.toString());
    }

    @Override
    public void contentModifiedUpCall(boolean modified, Path path) {
        if (modifiedProperty == null) return;
        modifiedProperty.set(modified);
    }

    @Override
    public void pathChanged(Path path) {
        if (downCallListener == null) return;
        downCallListener.pathChanged(path);
    }

    @Override
    public void setEditorHandListener(EditorHandListener listener) {
        this.downCallListener = listener;
    }

    public void setAddressPathProperty(StringProperty addressPathProperty) {
        this.addressPathProperty = addressPathProperty;
    }

    public void setModifiedProperty(BooleanProperty modifiedProperty) {
        this.modifiedProperty = modifiedProperty;
    }

    public void setDownCallListener(EditorHandListener downCallListener) {
        this.downCallListener = downCallListener;
    }

}
