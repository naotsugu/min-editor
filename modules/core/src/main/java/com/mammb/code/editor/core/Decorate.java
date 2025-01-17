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
package com.mammb.code.editor.core;

import com.mammb.code.editor.core.syntax.Syntax;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import com.mammb.code.editor.core.text.SubText;
import com.mammb.code.editor.core.text.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Decorate.
 * @author Naotsugu Kobayashi
 */
public interface Decorate {

    /**
     * Applies a style to the specified text.
     * @param text the text
     * @return list of style span
     */
    List<StyleSpan> apply(Text text);

    void addHighlights(int row, StyleSpan span);
    void clear();
    Set<Integer> highlightsRows();
    boolean isBlockScoped();

    /**
     * Create a new {@link Decorate}.
     * @param syntax the syntax
     * @return a new {@link Decorate}
     */
    static Decorate of(Syntax syntax) {
        return new DecorateImpl(syntax);
    }

    /**
     * The decorate implementation.
     */
    class DecorateImpl implements Decorate {

        /** The syntax. */
        private final Syntax syntax;

        /** The style spans keyed by row number. */
        private final Map<Integer, List<StyleSpan>> highlights = new HashMap<>();

        /**
         * Constructor.
         * @param syntax the syntax
         */
        public DecorateImpl(Syntax syntax) {
            this.syntax = syntax;
        }

        @Override
        public List<StyleSpan> apply(Text text) {
            return (text instanceof SubText sub)
                    ? apply(sub)
                    : apply(text.row(), text.value());
        }

        @Override
        public void addHighlights(int row, StyleSpan span) {
            highlights.computeIfAbsent(row, _ -> new ArrayList<>()).add(span);
        }

        @Override
        public void clear() {
            highlights.clear();
        }

        @Override
        public Set<Integer> highlightsRows() {
            return highlights.keySet();
        }

        @Override
        public boolean isBlockScoped() {
            return syntax.isBlockScoped();
        }

        private List<StyleSpan> apply(SubText sub) {
            List<StyleSpan> spans = new ArrayList<>();
            for (StyleSpan span : apply(sub.row(), sub.parent().value())) {
                if (span.offset() + span.length() <= sub.fromIndex() ||
                    sub.toIndex() <= span.offset()) {
                    continue;
                }
                spans.add(new StyleSpan(
                    span.style(),
                    Math.max(0, span.offset() - sub.fromIndex()),
                    span.length()
                ));
            }
            return spans;
        }

        private List<StyleSpan> apply(int row, String text) {
            List<StyleSpan> spans = highlights.getOrDefault(row, new ArrayList<>());
            spans.addAll(syntax.apply(row, text));
            return spans;
        }

    }
}
