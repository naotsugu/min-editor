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
    public PointText affectTranslate(PointText pointText) {
        if (!isSingleEdit()) {
            throw new UnsupportedOperationException();
        }
        // TODO
        return null;
    }

    @Override
    public boolean isSingleEdit() {
        return beforeText.indexOf('\n') == -1 && afterTText.indexOf('\n') == -1;
    }

}
