package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import java.util.Optional;

class LexerSources {

    static Optional<StyleSpan> readInlineBlock(LexerSource source, char ch, char escape, Style style) {
        var open = source.rollbackPeek().peek();
        char prev = source.next().ch();
        while (source.hasNext()) {
            var s = source.next();
            if (prev != escape && s.ch() == ch) {
                return Optional.of(
                    new StyleSpan(style, open.index(), s.index() - open.index() + 1));
            }
            prev = s.ch();
        }
        return Optional.empty();
    }

    static Optional<StyleSpan> readNumberLiteral(LexerSource source, Style style) {
        var open = source.rollbackPeek().peek();
        while (source.hasNext()) {
            var s = source.peek();
            if (!(Character.isDigit(s.ch()) || s.ch() == '.' || s.ch() == 'e' || s.ch() == 'E' || s.ch() == '_')) {
                return Optional.of(new StyleSpan(style, open.index(), s.index() - open.index()));
            }
            source.commitPeek();
        }
        return Optional.empty();
    }


}
