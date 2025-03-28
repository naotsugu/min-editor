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
package com.mammb.code.editor.core.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.mammb.code.editor.core.text.Style.StyleSpan;

/**
 * The styled text.
 * @author Naotsugu Kobayashi
 */
public interface StyledText extends Text {

    /**
     * Get the styles.
     * @return the styles
     */
    List<Style> styles();

    /**
     * Create the style text list.
     * @param text the source text
     * @param spans the style span
     * @return the style text list
     */
    static List<StyledText> of(Text text, List<StyleSpan> spans) {
        return new Builder(text).putAll(spans).buildLine();
    }

    /**
     * Create the style builder.
     * @param text the source text
     * @return the style builder
     */
    static Builder of(Text text) {
        return new Builder(text);
    }


    /**
     * The style builder.
     */
    class Builder {
        private final Text text;
        private final Set<Integer> bounds = new HashSet<>();
        private final List<StyleSpan> spans = new ArrayList<>();

        private Builder(Text text) {
            this.text = text;
        }

        public Builder putAll(List<StyleSpan> spans) {
            spans.forEach(this::put);
            return this;
        }

        public Builder put(StyleSpan span) {
            bounds.add(span.offset());
            bounds.add(span.offset() + span.length());
            spans.add(span);
            return this;
        }

        public List<StyledText> build() {
            return apply(text, 0, text.length());
        }

        public List<StyledText> buildLine() {
            if (text instanceof SubText sub) {
                return apply(sub, sub.fromIndex(), sub.toIndex());
            } else {
                return build();
            }
        }

        private List<StyledText> apply(Text text, int from, int to) {
            List<Integer> list = bounds.stream()
                    .filter(i -> from <= i && i <= to)
                    .sorted()
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                return List.of(new StyledTextWhole(text, List.of()));
            }
            if (list.getFirst() != from) {
                list.addFirst(from);
            }
            if (list.getLast() != to) {
                list.addLast(to);
            }

            List<StyledText> ret = new ArrayList<>();
            for (int i = 0; i < list.size() - 1; i++) {
                int start = list.get(i) - from;
                int end   = list.get(i + 1) - from;
                ret.add(new StyledTextPart(text, start, end, styles(list.get(i))));
            }
            return ret;
        }

        private List<Style> styles(int index) {
            return spans.stream()
                    .filter(span -> span.offset() <= index && index < (span.offset() + span.length()))
                    .map(StyleSpan::style)
                    .toList();
        }
    }



    class StyledTextWhole implements StyledText {
        private final Text peer;
        private final List<Style> styles;
        public StyledTextWhole(Text peer, List<Style> styles) {
            this.peer = peer;
            this.styles = styles;
        }
        @Override
        public int row() {
            return peer.row();
        }
        @Override
        public String value() {
            return peer.value();
        }
        @Override
        public double[] advances() {
            return peer.advances();
        }
        @Override
        public double width() {
            return peer.width();
        }
        @Override
        public double height() {
            return peer.height();
        }
        @Override
        public List<Style> styles() {
            return styles;
        }
    }

    class StyledTextPart implements StyledText {
        private final Text peer;
        private final int start;
        private final int end;
        private final List<Style> styles;
        public StyledTextPart(Text peer, int start, int end, List<Style> styles) {
            this.peer = peer;
            this.start = start;
            this.end = end;
            this.styles = styles;
        }
        @Override
        public int row() {
            return peer.row();
        }
        @Override
        public String value() {
            return peer.value().substring(start, end);
        }
        @Override
        public double[] advances() {
            return Arrays.copyOfRange(peer.advances(), start, end);
        }
        @Override
        public double width() {
            return Arrays.stream(peer.advances(), start, end).sum();
        }
        @Override
        public double height() {
            return peer.height();
        }
        @Override
        public List<Style> styles() {
            return styles;
        }
    }

}
