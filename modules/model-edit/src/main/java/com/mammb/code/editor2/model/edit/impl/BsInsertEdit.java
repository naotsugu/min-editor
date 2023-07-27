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
package com.mammb.code.editor2.model.edit.impl;

import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditTo;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

/**
 * Backspace undo insert Edit.
 * @param point the offset point of edit.
 * @param text the deletion text
 * @param occurredOn the occurredOn.
 * @author Naotsugu Kobayashi
 */
public record BsInsertEdit(
    OffsetPoint point,
    String text,
    long occurredOn) implements Edit {

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public Edit flip() {
        return new BsDeleteEdit(point, text, occurredOn);
    }

    @Override
    public void apply(EditTo editTo) {
        editTo.insert(point, text);
    }

    @Override
    public boolean canMerge(Edit other) {
        if (other instanceof EmptyEdit) {
            return true;
        }
        if (other.occurredOn() - occurredOn > 2000) {
            return false;
        }
        return (other instanceof BsInsertEdit insert) &&
            point.offset() + text.length() == insert.point().offset();
    }


    @Override
    public Edit merge(Edit other) {
        if (other instanceof EmptyEdit) {
            return this;
        }

        BsInsertEdit insert = (BsInsertEdit) other;
        return new InsertEdit(
            point,
            text + insert.text(),
            other.occurredOn());
    }

    @Override
    public boolean acrossRows() {
        return text.indexOf('\n') > -1;
    }

    @Override
    public Textual applyTo(Textual textual) {
        return new InsertEdit(point, text, occurredOn).applyTo(textual);
    }

}
