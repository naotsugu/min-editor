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
package com.mammb.code.editor2.model.editor.impl;

import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.editor.EditController;
import com.mammb.code.editor2.model.editor.ViewModel;
import com.mammb.code.editor2.model.style.StyledText;

/**
 * EditController.
 * @author Naotsugu Kobayashi
 */
public class EditControllerImpl implements EditController {

    private final ViewModel model;
    private TextBuffer<StyledText> buffer;

    public EditControllerImpl(ViewModel model, TextBuffer<StyledText> buffer) {
        this.model = model;
        this.buffer = buffer;
    }

    // --

    @Override
    public void input(String value) {
        buffer.push(Edit.insert(model.caret(), value));
    }

    @Override
    public String delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String backspace() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void redo() {
        throw new UnsupportedOperationException();
    }

}
