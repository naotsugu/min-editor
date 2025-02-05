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
package com.mammb.code.editor.core.syntax2.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mammb.code.editor.core.syntax2.BlockScopes;
import com.mammb.code.editor.core.syntax2.BlockToken;
import com.mammb.code.editor.core.syntax2.BlockType;
import com.mammb.code.editor.core.syntax2.LexerSource;
import com.mammb.code.editor.core.syntax2.Palette;
import com.mammb.code.editor.core.syntax2.Syntax;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.StyleSpan;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.LexerSources.readInlineBlock;

/**
 * The Markdown syntax.
 * @author Naotsugu Kobayashi
 */
public class MarkdownSyntax implements Syntax {

    /** The fence block. */
    private final BlockType fence = neutral("```", Syntax::of);
    /** The block scopes. */
    private final BlockScopes blockScopes = new BlockScopes(fence);

    @Override
    public String name() {
        return "md";
    }

    @Override
    public List<StyleSpan> apply(int row, String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {

            var block = blockScopes.read(source);
            if (block.isPresent()) {
                if (block.get().type() == fence &&
                    block.get().token() instanceof BlockToken.BlockTokenWith<?> token) {
                    return ((Syntax) token.with()).apply(row, text);
                }
            }
            if (!source.hasNext()) break;

            var peek = source.peek();
            char ch = peek.ch();

            if (ch == '`') {
                readInlineBlock(source, '`', '\\', Palette.darkGreen).ifPresent(spans::add);
            } else if (ch == '*') {
                spans.add(new Style.StyleSpan(Palette.darkOrange, peek.index(), 1));
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

    @Override
    public BlockScopes blockScopes() {
        return blockScopes;
    }
}
