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
package com.mammb.code.editor.ui.fx;

import com.mammb.code.editor.core.Config;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.ui.base.AppConfig;
import javafx.scene.Scene;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The application css.
 * @author Naotsugu Kobayashi
 */
public class AppCss {

    private final Theme theme;
    private final AppConfig config;

    private AppCss(Theme theme, AppConfig config) {
        this.theme = theme;
        this.config = config;
    }

    public static AppCss of(Theme theme, AppConfig config) {
        return new AppCss(theme, config);
    }

    public void apply(Scene scene) {
        scene.getStylesheets().add(String.join(",",
            "data:text/css;base64",
            Base64.getEncoder().encodeToString(css())));
    }

    private byte[] css() {
        return String.join("", rootCss(),
                textCss(), buttonCss(), scrollBarCss(), tabPaneCss(),
                contextMenuCss(), progressBarCss(), appCss())
            .getBytes(StandardCharsets.UTF_8);
    }

    private String rootCss() {
        return """
        .root {
          -fx-base:app-base;
          -fx-accent:app-accent;
          -fx-background:-fx-base;
          -fx-control-inner-background:app-back;
          -fx-control-inner-background-alt: derive(-fx-control-inner-background,-2%);
          -fx-focus-color: derive(-fx-control-inner-background,20%);
          -fx-faint-focus-color: -fx-focus-color;
          -fx-selection-bar-non-focused: derive(-fx-selection-bar, -50%);
          -fx-light-text-color:app-text;
          -fx-dark-text-color:app-text;
          -fx-mid-text-color: #333;
          -fx-mark-color: -fx-light-text-color;
          -fx-mark-highlight-color: derive(-fx-mark-color,20%);
          -fx-background-color:app-back;
          -fx-default-button: #2F65CA;
          -fx-font-family: "app-font-name";
          -fx-font-size: app-font-size;
          -fx-body-color: -fx-color;
        }
        """ .replaceAll("app-base", theme.baseColor().web()) // TODO theme vs config
            .replaceAll("app-text", theme.fgColor().web())
            .replaceAll("app-back", theme.baseColor().web())
            .replaceAll("app-accent", theme.paleHighlightColor().web())
            .replaceAll("app-font-name", config.uiFontName())
            .replaceAll("app-font-size", config.uiFontSize() + "px");
    }

    private String textCss() {
        return """
        .text-input, .label, .tooltip {
          -fx-font-size: 14px;
        }
        .text-input:focused {
          -fx-background-color: -fx-focus-color, -fx-control-inner-background;
        }
        .hyperlink, .hyperlink:hover, .hyperlink:hover:visited {
          -fx-text-fill: -fx-light-text-color;
          -fx-underline: true;
        }
        """;
    }

    private String buttonCss() {
        return """
        .button {
          -fx-background-color: -fx-body-color;
        }
        .button:hover {
          -fx-text-fill: white;
        }
        """;
    }

    private String scrollBarCss() {
        return """
        .scroll-bar {
          -fx-background-color: derive(-fx-box-border,30%)
        }
        .scroll-bar .thumb {
          -fx-background-color :derive(-fx-light-text-color, -50%);
          -fx-background-insets : 1.0, 0.0, 0.0;
        }
        .scroll-bar .thumb:hover {
          -fx-background-color :derive(-fx-light-text-color, -30%);
        }
        .scroll-bar .increment-button, .scroll-bar .decrement-button {
          -fx-background-color:transparent;
          -fx-background-radius:0;
        }
        .scroll-bar:vertical .decrement-button, .scroll-bar:horizontal .increment-button {
          -fx-padding:0 10 0 0;
        }
        .scroll-bar:vertical .increment-button, .scroll-bar:horizontal .decrement-button {
          -fx-padding:0 0 10 0;
        }
        .scroll-bar .increment-arrow, .scroll-bar .decrement-arrow {
          -fx-background-color:transparent;
          -fx-shape:"";
          -fx-padding:0;
        }
        """;
    }

    private String tabPaneCss() {
        return """
        .tab-pane {
          -fx-tab-min-height: 1.5em;
          -fx-tab-max-height: 1.5em;
        }
        .tab > .tab-label {
          -fx-font-size: 0.916667em;
        }
        .tab-pane > .tab-header-area > .headers-region > .tab {
          -fx-background-color: -fx-hover-base;
        }
        .tab-pane > .tab-header-area > .headers-region > .tab:selected {
          -fx-background-color: derive(-fx-box-border, 30%);
          -fx-border-color: derive(-fx-light-text-color, -30%) transparent transparent transparent;
        }
        .tab-pane > .tab-header-area > .headers-region > .tab:selected.tab-container-selected {
          -fx-border-color: -fx-light-text-color transparent transparent transparent;
        }
        .tab-pane:focused > .tab-header-area > .headers-region > .tab:selected .focus-indicator {
          -fx-border-width: 0;
        }
        .tab-pane > .tab-header-area > .tab-header-background {
          -fx-background-color: derive(-fx-text-box-border, 30%);
        }
        .tab-pane > .tab-header-area {
          -fx-padding: 0;
        }
        .split-pane > .split-pane-divider {
          -fx-pref-width: 3px;
        }
        """;
    }

    private String contextMenuCss() {
        return """
        .context-menu {
          -fx-font-family: "System";
          -fx-border-color: derive(-fx-base, 30%);
          -fx-border-width: 1px;
          -fx-border-radius: 8px;
        }
        .separator:horizontal .line {
          -fx-border-color: derive(-fx-base, 30%) transparent transparent transparent;
        }
        """;
    }

    private String progressBarCss() {
        return """
        .progress-bar > .bar {
          -fx-background-color: derive(-fx-accent, 50%);
          -fx-background-insets: 0;
          -fx-background-radius: 0;
        }
        .progress-bar > .track {
          -fx-background-color: transparent;
          -fx-background-insets: 0;
          -fx-background-radius: 0;
        }
        """;
    }


    private String appCss() {
        return """
        .app-command-palette-dialog-pane > .button-bar > .container {
          -fx-padding: 0;
        }
        """;
    }

}
