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

import com.mammb.code.editor2.model.edit.EditTo;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.edit.Edit;

/**
 * ReplaceEdit.
 * @param point the offset point of edit.
 * @param beforeText the before text string
 * @param afterTText the after text string
 * @param occurredOn the occurredOn.
 * @author Naotsugu Kobayashi
 */
public record ReplaceEdit(
        OffsetPoint point,
        String beforeText,
        String afterTText,
        long occurredOn) implements Edit {

    @Override
    public Edit flip() {
        return new ReplaceEdit(point, afterTText, beforeText, occurredOn);
    }

    @Override
    public void apply(EditTo editTo) {
        editTo.delete(point, beforeText.length());
        editTo.insert(point, afterTText);
    }

    @Override
    public Textual applyTo(Textual textual) {
        if (!isSingleEdit()) {
            throw new UnsupportedOperationException();
        }
        return switch (textual.compareOffsetRangeTo(point.offset())) {
            case -1 -> {
                OffsetPoint delta = OffsetPoint.of(
                    0,
                    afterTText.length() - beforeText.length(),
                    Character.codePointCount(afterTText, 0, afterTText.length())
                        - Character.codePointCount(beforeText, 0, beforeText.length()));
                yield Textual.of(textual.point().plus(delta), textual.text());
            }
            case  0 -> {
                StringBuilder sb = new StringBuilder(textual.text());
                int start = point.offset() - textual.point().offset();
                int end = start + beforeText.length();
                sb.replace(start, end, afterTText);
                yield Textual.of(textual.point(), sb.toString());
            }
            default -> textual;
        };
    }


    @Override
    public boolean isSingleEdit() {
        return beforeText.indexOf('\n') == -1 && afterTText.indexOf('\n') == -1;
    }

}
