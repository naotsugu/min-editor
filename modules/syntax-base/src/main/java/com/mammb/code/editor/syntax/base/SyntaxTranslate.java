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

import com.mammb.code.editor.model.style.Style;
import com.mammb.code.editor.model.style.StyleSpan;
import com.mammb.code.editor.model.style.StyledText;
import com.mammb.code.editor.model.style.StylingTranslate;
import com.mammb.code.editor.model.text.Textual;

import java.util.Objects;
import java.util.Optional;

import static java.lang.System.Logger.Level;

/**
 * SyntaxTranslate.
 * @author Naotsugu Kobayashi
 */
public class SyntaxTranslate implements StylingTranslate {

    /** logger. */
    private static final System.Logger log = System.getLogger(SyntaxTranslate.class.getName());

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
        this.scopes = ScopeTree.of(lexer.name());
        this.palette = new ColorPalette(baseColorString);
    }


    @Override
    public StyledText applyTo(Textual textual) {
        try {
            return applyToInternal(textual);
        } catch (Exception e) {
            log.log(Level.ERROR, "syntax translate error", e);
            return StyledText.of(textual);
        }
    }


    private StyledText applyToInternal(Textual textual) {

        final StyledText styledText = StyledText.of(textual);

        LexerSource source = LexerSource.of(textual);
        scopes.truncate(source.offset());
        lexer.setSource(source, scopes);

        currentContext().ifPresent(n ->
            styledText.putStyle(StyleSpan.of(new Style.Context(n.open().context()), 0)));

        Token prev = null;
        long beginPos = 0;
        for (Token token = lexer.nextToken(); !token.isEmpty(); token = lexer.nextToken()) {

            scopes.push(token);

            Token selected = selectToken(token, scopes.current());

            if (prev == null) {
                prev = selected;
                beginPos = token.position();
            } else {
                if (prev.type() != selected.type()) {
                    putStyle(styledText, prev, Math.toIntExact(beginPos - textual.offset()), Math.toIntExact(token.position() - beginPos));
                    prev = selected;
                    beginPos = token.position();
                }
            }
        }

        if (prev != null) {
            int pos = Math.toIntExact(beginPos - textual.offset());
            putStyle(styledText, prev, pos, textual.length() - pos);
        }

        currentContext().ifPresent(n -> {
            if (!textual.isEmpty())
                styledText.putStyle(StyleSpan.of(new Style.Context(n.open().context()), textual.length()));
        });

        return styledText;

    }


    private Token selectToken(Token current, ScopeNode scopeNode) {

        var contextNode = scopeNode.select(node -> !node.open().context().isEmpty()).orElseThrow();
        if (contextNode.open().context().equals(lexer.name())) {
            // if the current context is route lexer
            var prime = scopeNode.selectPrime(ScopeNode::isRoot);
            if (prime.isPresent()) {
                if (prime.get().open().type().serial() > current.type().serial()) {
                    return prime.get().open();
                } else {
                    return current;
                }
            } else {
                return current;
            }
        } else {
            // if the current context is different from the lexer context,
            // the delegated definition takes precedence
            var prime = scopeNode.selectPrime(node -> !node.open().context().isEmpty());
            if (prime.isPresent()) {
                if (prime.get().open().type().serial() > current.type().serial()) {
                    return prime.get().open();
                } else {
                    return current;
                }
            } else {
                if (current.type().hue().isColored()) {
                    return current;
                } else {
                    return contextNode.open();
                }
            }
        }
    }


    private Optional<ScopeNode> currentContext() {
        return scopes.current()
            .collect(n -> !n.open().context().isEmpty() && !n.open().context().contains("@"))
            .stream()
            .reduce((f, s) -> s);
    }


    private void putStyle(StyledText styledText, Token token, int point, int length) {
        var hue = token.type().hue();
        if (hue.isColored()) {
            Style style = new Style.Color(palette.on(hue), 1.0);
            StyleSpan styleSpan = StyleSpan.of(style, point, length);
            styledText.putStyle(styleSpan);
        }
    }

}
