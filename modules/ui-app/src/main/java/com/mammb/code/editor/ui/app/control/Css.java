package com.mammb.code.editor.ui.app.control;

import java.util.Arrays;

public interface Css {

    Css empty = st -> CssRun.empty;

    CssRun on(StyleTheme st);

    static Css join(Css... css) {
        return Arrays.stream(css).reduce((a, b) -> a.join(b)).orElse(empty);
    }

    default Css join(Css other) {
        return st -> on(st).join(other.on(st));
    }

}
