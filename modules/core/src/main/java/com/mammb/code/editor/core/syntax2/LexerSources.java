/*
 * Copyright 2022-2025 the original author or authors.
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
package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import java.util.Optional;

/**
 * The lexer sources.
 * @author Naotsugu Kobayashi
 */
class LexerSources {

    static Optional<StyleSpan> readInlineBlock(LexerSource source, char ch, char escape, Style style) {
        var open = source.rollbackPeek().peek();
        char prev = source.next().ch();
        while (source.hasNext()) {
            var s = source.next();
            if (prev != escape && s.ch() == ch) {
                return Optional.of(
                    new StyleSpan(style, open.index(), s.index() - open.index() + 1));
            }
            prev = s.ch();
        }
        return Optional.empty();
    }

    static Optional<StyleSpan> readNumberLiteral(LexerSource source, Style style) {
        var open = source.rollbackPeek().peek();
        while (source.hasNext()) {
            var s = source.peek();
            if (!(Character.isDigit(s.ch()) || s.ch() == '.' || s.ch() == 'e' || s.ch() == 'E' || s.ch() == '_')) {
                return Optional.of(new StyleSpan(style, open.index(), s.index() - open.index()));
            }
            source.commitPeek();
        }
        return Optional.empty();
    }


}
