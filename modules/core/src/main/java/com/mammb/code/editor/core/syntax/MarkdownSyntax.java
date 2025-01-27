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
package com.mammb.code.editor.core.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.syntax.BlockScopes.BlockType;
import com.mammb.code.editor.core.syntax.LexerSource.Indexed;
import com.mammb.code.editor.core.text.Style;

/**
 * The Markdown syntax.
 * @author Naotsugu Kobayashi
 */
public class MarkdownSyntax implements Syntax {

    private final BlockScopes scopes = new BlockScopes();
    private final String FENCE = "```";

    @Override
    public String name() {
        return "md";
    }

    @Override
    public List<Style.StyleSpan> apply(int row, String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<Style.StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            Indexed peek = source.peek();
            char ch = peek.ch();
            Optional<BlockType> block = scopes.inScope(source.row(), peek.index());
            Optional<BlockType> fence = block.filter(t -> t.open().startsWith(FENCE));

            if (fence.isPresent()) {
                if (ch == '`' && source.match(FENCE) && peek.index() == 0) {
                    var s = source.nextRemaining();
                    scopes.putNeutral(source.row(), s.index(), BlockType.neutral(FENCE, Syntax.of("")));
                    return spans;
                } else {
                    Syntax syntax = (Syntax) ((BlockScopes.NeutralAttributed) fence.get()).attribute();
                    return syntax.apply(row, text);
                }
            }

            if (ch == '`' && source.match(FENCE)) {
                var s = source.nextRemaining();
                Syntax syntax = Syntax.of(s.string().substring(3).trim());
                scopes.putNeutral(source.row(), s.index(), BlockType.neutral("```", syntax));

            } else if (ch == '#' && peek.index() == 0) {
                Style style;
                if (source.match("#####"))     style = Palette.darkPale;
                else if (source.match("####")) style = Palette.darkPale;
                else if (source.match("###"))  style = Palette.darkPale;
                else if (source.match("##"))   style = Palette.darkPale;
                else style = Palette.darkOrange;
                var s = source.nextRemaining();
                spans.add(new Style.StyleSpan(style, s.index(), s.length()));
            }
            source.commitPeek();
        }
        return spans;
    }
}
