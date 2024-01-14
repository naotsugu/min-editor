/*
 * Copyright 2023-2024 the original author or authors.
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
import javafx.scene.Parent;
import javafx.scene.Scene;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

/**
 * The theme css.
 * @author Naotsugu Kobayashi
 */
public class ThemeCss {

    /** The style theme. */
    static volatile StyleTheme styleTheme = StyleTheme.dark();

    /** The css run. */
    private CssRun cssRun;


    /**
     * Constructor.
     * @param cssRun the css run
     */
    private ThemeCss(CssRun cssRun) {
        this.cssRun = cssRun;
    }


    /**
     * Set the scheme.
     * @param cs the color scheme
     */
    public static void install(ColorScheme cs) {
        styleTheme = switch (cs) {
            case DARK -> StyleTheme.dark();
            case LIGHT -> StyleTheme.light();
        };
    }


    /**
     * Create the theme css.
     * @return the StCss
     */
    public static ThemeCss of() {
        var css = Css.join(root, UiIcon.css, UiFlatButton.css, UiPromptText.css);
        return new ThemeCss(css.on(styleTheme));
    }


    /**
     * Create the theme css.
     * @param css the css
     * @return the StCss
     */
    public static ThemeCss of(Css css) {
        return new ThemeCss(css.on(styleTheme));
    }


    /**
     * Create the root theme css.
     * @return the StCss
     */
    public static ThemeCss rootOf() {
        return of(root);
    }


    /**
     * Apply the stylesheet from this css text.
     * @param scene the scene
     */
    public void into(Scene scene) {
        cssRun.into(scene);
    }


    /**
     * Apply the stylesheet from this css text.
     * @param parent the parent
     */
    public void into(Parent parent) {
        cssRun.into(parent);
    }


    /** The root css. */
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
        .text-input, .label {
          -fx-font: 14px "Consolas";
        }
        """;

}
