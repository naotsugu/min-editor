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
package com.mammb.code.editor.syntax.base;

import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyleSpan;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor.syntax.base.impl.LexicalScope;

import java.util.Objects;

/**
 * SyntaxTranslate.
 * @author Naotsugu Kobayashi
 */
public class SyntaxTranslate implements StylingTranslate {

    /** The lexer. */
    private final Lexer lexer;

    /** The scopes. */
    private final LexicalScope scopes = new LexicalScope();

    /** The ColorPalette. */
    private final ColorPalette palette;


    /**
     * Create a new {@link SyntaxTranslate}.
     * @param lexer the source lexer
     */
    public SyntaxTranslate(Lexer lexer) {
        this(lexer, "");
    }


    /**
     * Create a new {@link SyntaxTranslate}.
     * @param lexer the source lexer
     * @param baseColorString the base color string((like #808080))
     */
    public SyntaxTranslate(Lexer lexer, String baseColorString) {
        this.lexer = Objects.requireNonNull(lexer);
        this.palette = new ColorPalette(baseColorString);
    }


    @Override
    public StyledText applyTo(Textual textual) {

        final StyledText styledText = StyledText.of(textual);

        scopes.start(textual.offset());
        lexer.setSource(LexerSource.of(textual));

        Token prev = null;
        int beginPos = 0;

        for (Token token = lexer.nextToken();
             !token.isEmpty();
             token = lexer.nextToken()) {

            if (!token.scope().isNeutral()) {
                scopes.put(token.position(), token);
            }

            Token current = scopes.current().orElse(token);

            if (prev == null) {
                prev = current;
                beginPos = token.position();
            } else {
                if (prev.type() != current.type()) {
                    var hue = prev.type().hue();
                    if (hue != Hue.NONE) {
                        styledText.putStyle(StyleSpan.of(
                            new Style.Color(palette.on(hue), 1.0), beginPos - textual.offset(), token.position() - beginPos));
                    }
                    prev = current;
                    beginPos = token.position();
                }
            }
        }

        if (prev != null) {
            var hue = prev.type().hue();
            if (hue != Hue.NONE) {
                int pos = beginPos - textual.offset();
                styledText.putStyle(StyleSpan.of(
                    new Style.Color(palette.on(hue), 1.0), pos, textual.length() - pos));
            }
        }

        return styledText;
    }
}
