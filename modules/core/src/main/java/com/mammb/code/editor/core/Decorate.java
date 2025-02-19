/*
 * Copyright 2023-2025 the original author or authors.
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

import com.mammb.code.editor.core.syntax2.LexerSource;
import com.mammb.code.editor.core.syntax2.Syntax;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import com.mammb.code.editor.core.text.SubText;
import com.mammb.code.editor.core.text.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    /**
     * Add a highlight on the specified row.
     * @param row the number of row
     * @param span the style span
     */
    void addHighlights(int row, StyleSpan span);

    /**
     * Clear the decoration.
     */
    void clear();

    /**
     * Get the highlighted row.
     * @return the highlighted row
     */
    Set<Integer> highlightsRows();

    boolean isBlockScoped();

    void warmApply(int row, int len, Content content);

    /**
     * Get the syntax name.
     * @return the syntax name
     */
    String syntaxName();

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
        private DecorateImpl(Syntax syntax) {
            this.syntax = syntax;
        }

        @Override
        public List<StyleSpan> apply(Text text) {
            if (text instanceof SubText sub) {
                text = sub.parent();
            }
            List<StyleSpan> spans = highlights.getOrDefault(text.row(), new ArrayList<>());
            spans.addAll(syntax.apply(text.row(), text.value()));
            return spans;
        }

        @Override
        public void warmApply(int row, int len, Content content) {
            if (syntax.hasBlockScopes() && len > 0) {
                syntax.blockScopes().put(new Iterator<>() {
                    int index = row;
                    @Override
                    public boolean hasNext() {
                        return index < (row + len);
                    }
                    @Override
                    public LexerSource next() {
                        return LexerSource.of(index, content.getText(index++));
                    }
                });
            }
        }

        @Override
        public String syntaxName() {
            return syntax.name();
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
            return syntax.hasBlockScopes();
        }

    }
}
