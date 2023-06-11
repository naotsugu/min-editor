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

import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.PointText;
import com.mammb.code.editor2.model.edit.Edit;

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
    public Edit flip() {
        return new InsertEdit(point, text, occurredOn);
    }


    @Override
    public PointText applyTo(PointText pointText) {
        if (!isSingleEdit()) {
            throw new UnsupportedOperationException();
        }
        return switch (pointText.compareOffsetRangeTo(point.offset())) {
            case -1 -> {
                OffsetPoint delta = OffsetPoint.of(
                    0,
                        -text.length(),
                        -Character.codePointCount(text, 0, text.length()));
                yield PointText.of(pointText.point().plus(delta), pointText.text());
            }
            case  0 -> {
                StringBuilder sb = new StringBuilder(pointText.text());
                int start = point.offset() - pointText.point().offset();
                int end = start + text.length();
                sb.replace(start, end, "");
                yield PointText.of(pointText.point(), sb.toString());
            }
            default -> pointText;
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
    public boolean isSingleEdit() {
        return text.indexOf('\n') == -1;
    }

}
