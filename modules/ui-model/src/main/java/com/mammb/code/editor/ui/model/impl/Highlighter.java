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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.find.Find;
import com.mammb.code.editor.model.find.FoundRun;
import com.mammb.code.editor.model.style.HighlightTranslate;
import com.mammb.code.editor.model.style.Style;
import com.mammb.code.editor.model.style.StyleSpan;
import com.mammb.code.editor.model.style.StyledText;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * The highlight translate.
 * @author Naotsugu Kobayashi
 */
public class Highlighter implements HighlightTranslate, Consumer<FoundRun> {

    private TreeMap<Long, FoundRun> founds = new TreeMap<>();

    private Style style = new Style.BgColor("FFCDD2", 0.2);

    private final Find find;

    public Highlighter(Find find) {
        this.find = find;
        find.addListener(this);
    }

    public static Highlighter of(Find find) {
        return new Highlighter(find);
    }

    @Override
    public StyledText applyTo(StyledText styledText) {
        for (FoundRun foundRun : founds.subMap(
            styledText.offset(),
            styledText.tailOffset()).values()) {
            styledText.putStyle(StyleSpan.of(style,
                Math.toIntExact(foundRun.chOffset() - styledText.offset()),
                foundRun.length()));
        }
        return styledText;
    }

    @Override
    public void accept(FoundRun foundRun) {
        founds.put(foundRun.chOffset(), foundRun);
    }

    public void clear() {
        founds.clear();
    }
}
