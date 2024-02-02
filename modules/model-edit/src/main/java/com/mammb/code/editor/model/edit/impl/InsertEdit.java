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
 * InsertEdit.
 * @param point the offset point of edit.
 * @param text the deletion text
 * @param occurredOn the occurredOn.
 * @author Naotsugu Kobayashi
 */
public record InsertEdit(
        OffsetPoint point,
        String text,
        long occurredOn) implements Edit {

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public Edit flip() {
        return new DeleteEdit(point, text, occurredOn);
    }

    @Override
    public void apply(EditTo editTo) {
        editTo.insert(point, text);
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
                    textual.offsetPoint().offset() + text.length(),
                    textual.offsetPoint().cpOffset() + Character.codePointCount(text, 0, text.length()));
                yield Textual.of(newPoint, textual.text());
            }
            case 0 -> {
                StringBuilder sb = new StringBuilder(textual.text());
                sb.insert(Math.toIntExact(point.offset() - textual.offsetPoint().offset()), text);
                yield Textual.of(textual.offsetPoint(), sb.toString());
            }
            case 1 -> {
                if (textual.tailOffset() == point.offset() && textual.endMarkCount() == 0)
                    yield Textual.of(textual.offsetPoint(), textual.text() + text);
                else
                    yield textual;
            }
            default -> textual;
        };

    }


    @Override
    public boolean canMerge(Edit other) {
        if (other instanceof EmptyEdit) {
            return true;
        }
        if (other.occurredOn() - occurredOn > 2500) {
            return false;
        }
        return (other instanceof InsertEdit insert) &&
            point.offset() + text.length() == insert.point().offset();
    }


    @Override
    public Edit merge(Edit other) {
        if (other instanceof EmptyEdit) {
            return this;
        }

        InsertEdit insert = (InsertEdit) other;
        return new InsertEdit(
            point,
            text + insert.text(),
            other.occurredOn());
    }

    @Override
    public boolean acrossRows() {
        return text.indexOf('\n') > -1;
    }

}
