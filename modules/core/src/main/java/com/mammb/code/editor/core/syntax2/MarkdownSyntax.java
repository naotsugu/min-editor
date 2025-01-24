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
package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.text.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;

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
    public List<Style.StyleSpan> apply(int row, String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        var spans = new ArrayList<Style.StyleSpan>();
        var source = LexerSource.of(row, text);

        while (source.hasNext()) {
            source.commitPeek();
        }

        return spans;
    }
}
