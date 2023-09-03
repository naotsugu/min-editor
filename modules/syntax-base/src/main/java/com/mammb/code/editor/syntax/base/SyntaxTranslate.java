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

import com.mammb.code.editor.syntax.base.impl.ScopeTreeImpl;
import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyleSpan;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;

import java.util.Objects;

/**
 * SyntaxTranslate.
 * @author Naotsugu Kobayashi
 */
public class SyntaxTranslate implements StylingTranslate {

    /** The lexer. */
    private final Lexer lexer;

    /** The scopes. */
    private final ScopeTree scopes;

    /** The ColorPalette. */
    private final ColorPalette palette;


    /**
     * Create a new {@link SyntaxTranslate}.
     * @param lexer the source lexer
     * @param baseColorString the base color string((like #808080))
     */
    public SyntaxTranslate(Lexer lexer, String baseColorString) {
        this.lexer = Objects.requireNonNull(lexer);
        this.scopes = ScopeTreeImpl.of(lexer.name());
        this.palette = new ColorPalette(baseColorString);
    }


    @Override
    public StyledText applyTo(Textual textual) {

        final StyledText styledText = StyledText.of(textual);

        lexer.setSource(LexerSource.of(textual), scopes);

        Token prev = null;
        int beginPos = 0;

        for (Token token = lexer.nextToken(); !token.isEmpty(); token = lexer.nextToken()) {

            scopes.push(token);

            ScopeNode scopeNode = scopes.primeInContext();
            Token current = (scopeNode == null) ? token : scopeNode.open();

            if (prev == null) {
                prev = current;
                beginPos = token.position();
            } else {
                if (prev.type() != current.type()) {
                    putStyle(styledText, prev, beginPos - textual.offset(), token.position() - beginPos);
                    prev = current;
                    beginPos = token.position();
                }
            }
        }

        if (prev != null) {
            int pos = beginPos - textual.offset();
            putStyle(styledText, prev, pos, textual.length() - pos);
        }

        return styledText;
    }


    private void putStyle(StyledText styledText, Token token, int point, int length) {
        var hue = token.type().hue();
        if (hue != Hue.NONE) {
            Style style = new Style.Color(palette.on(hue), 1.0);
            StyleSpan styleSpan = StyleSpan.of(style, point, length);
            styledText.putStyle(styleSpan);
        }
    }

}
