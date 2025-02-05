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
package com.mammb.code.editor.core.syntax2.lang;

import com.mammb.code.editor.core.syntax2.LexerSource;
import com.mammb.code.editor.core.syntax2.Palette;
import com.mammb.code.editor.core.syntax2.Syntax;
import com.mammb.code.editor.core.text.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mammb.code.editor.core.syntax2.LexerSources.readInlineBlock;

/**
 * The yaml syntax.
 * @author Naotsugu Kobayashi
 */
public class YamlSyntax implements Syntax {

    @Override
    public String name() {
        return "yaml";
    }

    @Override
    public List<Style.StyleSpan> apply(int row, String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<Style.StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            var peek = source.peek();

            if (peek.ch() == '#') {
                var s = source.nextRemaining();
                spans.add(new Style.StyleSpan(Palette.gray, s.index(), s.length()));

            } else if (peek.ch() == '"') {
                readInlineBlock(source, '"', '\\', Palette.darkGreen).ifPresent(spans::add);

            } else if (Character.isAlphabetic(peek.ch())) {
                var s = source.nextUntilWs();
                if (s.text().endsWith(":")) {
                    spans.add(new Style.StyleSpan(Palette.darkOrange, s.index(), s.length() - 1));
                }
            }

            source.commitPeek();
        }

        return spans;

    }
}
