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
package com.mammb.code.editor.model.layout;

import com.mammb.code.editor.model.text.Translate;
import java.util.List;
import java.util.Objects;

/**
 * LayoutTranslate.
 * @author Naotsugu Kobayashi
 */
public class LayoutTranslate implements Translate<List<Span>, TextLine> {

    /** The line layout. */
    private final LineLayout lineLayout;

    /**
     * Constructor.
     * @param lineLayout the line layout
     */
    private LayoutTranslate(LineLayout lineLayout) {
        this.lineLayout = Objects.requireNonNull(lineLayout);
    }

    /**
     * Create a new translation.
     * @param lineLayout the line layout
     * @return a new translation
     */
    public static Translate<List<Span>, TextLine> of(LineLayout lineLayout) {
        return new LayoutTranslate(lineLayout);
    }

    @Override
    public TextLine applyTo(List<Span> row) {
        List<TextLine> results = lineLayout.layout(row);
        return results.isEmpty() ? null : results.getFirst();
    }

}
