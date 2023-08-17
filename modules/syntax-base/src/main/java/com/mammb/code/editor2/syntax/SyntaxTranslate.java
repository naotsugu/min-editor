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
package com.mammb.code.editor2.syntax;

import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyleSpan;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.syntax.impl.LexicalScope;

/**
 * SyntaxTranslate.
 * @author Naotsugu Kobayashi
 */
public class SyntaxTranslate implements StylingTranslate {

    /** The lexer. */
    private final Lexer lexer;

    /** The scopes. */
    private final LexicalScope scopes = new LexicalScope();


    public SyntaxTranslate(Lexer lexer) {
        this.lexer = lexer;
    }


    @Override
    public StyledText applyTo(Textual textual) {

        final StyledText styledText = StyledText.of(textual);

        scopes.init(textual.offset());
        lexer.setSource(LexerSource.of(textual));


        Token prev = null;
        int beginOffset = 0;

        for (Token token = lexer.nextToken();
             !token.isEmpty();
             token = lexer.nextToken()) {

            if (!token.scope().isNeutral()) {
                scopes.put(token, textual.offset() + token.position());
            }

            Token current = scopes.current();
            current = (current == null) ? token : current;

            if (prev == null) {
                prev = current;
                beginOffset = token.position();
            } else {
                if (prev.type() != current.type()) {
                    var cs = prev.type().colorString();
                    if (!cs.isEmpty()) {
                        styledText.putStyle(StyleSpan.of(
                            new Style.Color(cs, 1.0), beginOffset, token.position() - beginOffset));
                    }
                    prev = current;
                    beginOffset = token.position();
                }
            }
        }

        if (prev != null) {
            var cs = prev.type().colorString();
            if (!cs.isEmpty()) {
                styledText.putStyle(StyleSpan.of(
                    new Style.Color(cs, 1.0), beginOffset, textual.length() - beginOffset));
            }
        }

        return styledText;
    }
}
