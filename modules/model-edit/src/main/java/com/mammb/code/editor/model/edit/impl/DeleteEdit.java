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
 * DeleteEdit.
 * @param point the offset point of edit.
 * @param text the deletion text
 * @param occurredOn the occurredOn.
 * @author Naotsugu Kobayashi
 */
public record DeleteEdit(
        OffsetPoint point,
        String text,
        long occurredOn) implements Edit {

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public Edit flip() {
        return new InsertEdit(point, text, occurredOn);
    }

    @Override
    public void apply(EditTo editTo) {
        editTo.delete(point, text);
    }

    @Override
    public Textual applyTo(Textual textual) {
        if (acrossRows()) {
            throw new IllegalStateException("Should be pre-flashed");
        }
        return switch (textual.compareOffsetRangeTo(point.offset())) {
            case -1 -> {
                OffsetPoint newPoint = OffsetPoint.of(
                    textual.offsetPoint().row(),
                    textual.offsetPoint().offset() - text.length(),
                    textual.offsetPoint().offset() - Character.codePointCount(text, 0, text.length())
                );
                yield Textual.of(newPoint, textual.text());
            }
            case  0 -> {
                StringBuilder sb = new StringBuilder(textual.text());
                int start = Math.toIntExact(point.offset() - textual.offsetPoint().offset());
                int end = start + text.length();
                sb.replace(start, end, "");
                yield Textual.of(textual.offsetPoint(), sb.toString());
            }
            default -> textual;
        };
    }


    @Override
    public boolean canMerge(Edit other) {
        if (other instanceof EmptyEdit) {
            return true;
        }
        if (other.occurredOn() - occurredOn > 2000) {
            return false;
        }
        return (other instanceof DeleteEdit delete) &&
            point.offset() == delete.point().offset();
    }


    @Override
    public Edit merge(Edit other) {
        if (other instanceof EmptyEdit) {
            return this;
        }
        DeleteEdit delete = (DeleteEdit) other;
        return new DeleteEdit(
            point,
            text + delete.text(),
            other.occurredOn());
    }

    @Override
    public boolean acrossRows() {
        return text.indexOf('\n') > -1;
    }

}
