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
package com.mammb.code.editor.core.syntax.lang;

import com.mammb.code.editor.core.syntax.LexerSource;
import com.mammb.code.editor.core.syntax.Palette;
import com.mammb.code.editor.core.syntax.Syntax;
import com.mammb.code.editor.core.text.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The csv syntax.
 * @author Naotsugu Kobayashi
 */
public class CsvSyntax implements Syntax {

    @Override
    public String name() {
        return "csv";
    }

    @Override
    public List<Style.StyleSpan> apply(int row, String text) {

        if (text == null) {
            return Collections.emptyList();
        }

        boolean inQuote = false;
        var spans = new ArrayList<Style.StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            var peek = source.peek();

            if (peek.ch() == '"') {
                inQuote = !inQuote;
            } else if (peek.ch() == ',' && !inQuote) {
                spans.add(new Style.StyleSpan(Palette.gray, peek.index(), 1));
            }

            source.commitPeek();
        }

        return spans;
    }

}
