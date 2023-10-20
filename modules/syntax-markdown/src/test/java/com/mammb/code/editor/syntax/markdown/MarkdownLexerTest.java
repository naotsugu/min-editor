/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.syntax.markdown;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.PassThroughLexer;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link MarkdownLexer}.
 * @author Naotsugu Kobayashi
 */
class MarkdownLexerTest {

    @Test void nextToken() {
        var target = new MarkdownLexer(PassThroughLexer::new);
        var source = LexerSource.of(Textual.of(OffsetPoint.zero, "```j\n"));
        var scope = ScopeTree.of("md");
        target.setSource(source, scope);

        Token token = target.nextToken();
        assertEquals(Markdown.MdToken.FENCE, token.type());
    }

}
