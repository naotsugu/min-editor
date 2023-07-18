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
package com.mammb.code.editor2.ui.pane.impl;

import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyleSpan;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;
import com.mammb.code.editor2.ui.pane.Selections;
import java.util.Objects;

/**
 * SelectionTranslate.
 * @author Naotsugu Kobayashi
 */
public class SelectionTranslate implements Translate<StyledText, StyledText> {

    private final Style bgColor = new Style.BgColor("#42A5F5");

    private final Selections selections;


    public SelectionTranslate(Selections selections) {
        this.selections = Objects.requireNonNull(selections);
    }

    @Override
    public StyledText applyTo(StyledText input) {
        selections.forEach(sel -> {
            if (sel.length() > 0 && sel.max() >= input.point().offset() && sel.min() < input.tailOffset()) {
                int start = Math.max(0, sel.min() - input.point().offset());
                int end = Math.min(input.text().length(), sel.max() - input.point().offset());
                input.putStyle(StyleSpan.of(bgColor, start, end - start));
            }
        });
        return input;
    }

}
