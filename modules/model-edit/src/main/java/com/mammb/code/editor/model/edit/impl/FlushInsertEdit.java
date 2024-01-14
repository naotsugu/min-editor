/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.model.edit.impl;

import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditTo;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;

/**
 * FlushInsertEdit.
 * @param point the offset point of edit.
 * @param text the deletion text
 * @param occurredOn the occurredOn.
 * @author Naotsugu Kobayashi
 */
public record FlushInsertEdit(
    OffsetPoint point,
    String text,
    long occurredOn) implements Edit {

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public Edit flip() {
        return Edit.empty;
    }

    @Override
    public void apply(EditTo editTo) {
    }

    @Override
    public Textual applyTo(Textual textual) {
        return new InsertEdit(point, text, occurredOn).applyTo(textual);
    }

    @Override
    public boolean canMerge(Edit other) {
        return false;
    }

    @Override
    public Edit merge(Edit other) {
        return other;
    }

    @Override
    public boolean acrossRows() {
        return false;
    }

}
