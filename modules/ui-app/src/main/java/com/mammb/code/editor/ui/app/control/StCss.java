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
package com.mammb.code.editor.ui.app.control;

import com.mammb.code.editor.ui.prefs.ColorScheme;
import javafx.scene.Scene;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

/**
 * The StCss.
 * @author Naotsugu Kobayashi
 */
public class StCss {

    private CssRun cssRun;

    private StCss(StyleTheme st) {
        cssRun = Css.join(root, Icon.css, FlatButton.css).on(st);
    }

    public static StCss of(ColorScheme cs) {
        return switch (cs) {
            case DARK  -> new StCss(StyleTheme.dark());
            case LIGHT -> new StCss(StyleTheme.light());
        };
    }

    public void into(Scene scene) {
        cssRun.into(scene);
    }

    private static final Css root = st -> CSS."""
        .root {
          -fx-base:\{st.base};
          -fx-accent:\{st.accent};
          -fx-background:-fx-base;
          -fx-control-inner-background:\{st.back};
          -fx-control-inner-background-alt: derive(-fx-control-inner-background,-2%);
          -fx-focus-color: -fx-accent;
          -fx-faint-focus-color:\{st.accent}22;
          -fx-light-text-color:\{st.text};
          -fx-mark-color: -fx-light-text-color;
          -fx-mark-highlight-color: derive(-fx-mark-color,20%);
        }
        """;

}
