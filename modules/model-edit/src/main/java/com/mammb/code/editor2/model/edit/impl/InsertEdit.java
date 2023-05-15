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

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.edit.Edit;

import java.util.List;

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
    public Edit flip() {
        return new DeleteEdit(point, text, occurredOn);
    }


    @Override
    public PointText affectTranslate(PointText pointText) {
        if (!isSingleEdit()) {
            throw new UnsupportedOperationException();
        }
        // |0|1|      tailOffset:2
        // |2|3|4|5|  tailOffset:6
        // |6|
        if (pointText.tailOffset() <= point.offset()) {
            return pointText;
        } else if (pointText.tailOffset() > point.offset() &&
                pointText.point().offset() <= point.offset()) {
            StringBuilder sb = new StringBuilder(pointText.text());
            sb.insert(point.offset() - pointText.point().offset(), text);
            return PointText.of(pointText.point(), sb.toString());
        } else {
            OffsetPoint delta = OffsetPoint.of(
                0, text.length(), Character.codePointCount(text, 0, text.length()));
            return PointText.of(pointText.point().plus(delta), pointText.text());
        }
    }


    @Override
    public boolean canMerge(Edit other) {
        if (other instanceof EmptyEdit) {
            return true;
        }
        if (other.occurredOn() - occurredOn > 2000) {
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
    public boolean isSingleEdit() {
        return text.indexOf('\n') == -1;
    }

}
