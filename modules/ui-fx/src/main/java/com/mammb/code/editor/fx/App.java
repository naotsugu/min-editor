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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * The application.
 * @author Naotsugu Kobayashi
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        Parameters params = getParameters();
        var ctx = new AppContext();
        stage.setX(ctx.config().windowPositionX());
        stage.setY(ctx.config().windowPositionY());
        var appPane = new AppPane(stage, ctx);
        Scene scene = new Scene(appPane, ctx.config().windowWidth(), ctx.config().windowHeight());
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle(Version.appName);
        stage.getIcons().add(new Image(
            Objects.requireNonNull(App.class.getResourceAsStream("/icon.png"))));
        setupSyncConfigProperty(stage, scene, ctx);
        stage.show();
    }

    private void setupSyncConfigProperty(Stage stage, Scene scene, AppContext ctx) {
        scene.heightProperty().addListener((_, _, height) -> ctx.config().windowHeight(height.doubleValue()));
        scene.widthProperty().addListener((_, _, width) -> ctx.config().windowWidth(width.doubleValue()));
        stage.xProperty().addListener((_, _, x) -> ctx.config().windowPositionX(x.doubleValue()));
        stage.yProperty().addListener((_, _, y) -> ctx.config().windowPositionY(y.doubleValue()));
    }

    /** The app css. */
    private static String css = String.join(",", "data:text/css;base64",
        Base64.getEncoder().encodeToString("""
        .root {
          -fx-base:app-base;
          -fx-accent:app-accent;
          -fx-background:-fx-base;
          -fx-control-inner-background:app-back;
          -fx-control-inner-background-alt: derive(-fx-control-inner-background,-2%);
          -fx-focus-color: -fx-accent;
          -fx-faint-focus-color:app-accent;
          -fx-light-text-color:app-text;
          -fx-mark-color: -fx-light-text-color;
          -fx-mark-highlight-color: derive(-fx-mark-color,20%);
          -fx-background-color:app-back;
          -fx-default-button: #365880;
        }
        .text-input, .label {
          -fx-font: 14px "Consolas";
        }
        .button {
          -fx-background-color: /* -fx-shadow-highlight-color, */ -fx-outer-border, -fx-inner-border, -fx-body-color;
        }

        .app-command-palette-dialog-pane > .button-bar > .container {
            -fx-padding: 0;
        }
        .menu-bar {
          -fx-use-system-menu-bar:true;
          -fx-background-color:derive(-fx-control-inner-background,20%);
        }
        .scroll-bar {
            -fx-background-color: derive(-fx-box-border,30%)
        }
        .scroll-bar .thumb {
            -fx-background-color :-fx-inner-border;
            -fx-background-insets : 1.0, 0.0, 0.0;
        }
        .scroll-bar .increment-button,
        .scroll-bar .decrement-button {
            -fx-background-color:transparent;
            -fx-background-radius:0;
        }
        .scroll-bar:vertical .decrement-button {
            -fx-padding:0 10 0 0;
        }
        .scroll-bar:vertical .increment-button {
            -fx-padding:0 0 10 0;
        }
        .scroll-bar:horizontal .decrement-button {
            -fx-padding:0 0 10 0;
        }
        .scroll-bar:horizontal .increment-button {
            -fx-padding:0 10 0 0;
        }
        .scroll-bar .increment-arrow,
        .scroll-bar .decrement-arrow {
            -fx-background-color:transparent;
            -fx-shape:"";
            -fx-padding:0;
        }

        .tab-pane > .tab-header-area > .headers-region > .tab {
            -fx-background-color: -fx-hover-base;
        }
        .app-tab-pane-active > .tab-pane > .tab-header-area > .headers-region > .tab:selected {
            -fx-background-color: derive(-fx-box-border, 30%);
        }
        .tab-pane > .tab-header-area > .headers-region > .tab:selected {
            -fx-color: -fx-hover-base;
        }
        .tab-pane > .tab-header-area > .tab-header-background {
            -fx-background-color: derive(-fx-text-box-border, 30%);
        }
        .tab-pane > .tab-header-area {
            -fx-padding: 0;
        }
        .tab-label {
          -fx-font: 13px "Consolas";
        }

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
        """
        .replaceAll("app-base", Theme.dark.baseColor())
        .replaceAll("app-text", Theme.dark.fgColor())
        .replaceAll("app-back", Theme.dark.baseColor())
        .replaceAll("app-accent", Theme.dark.paleHighlightColor())
        .getBytes(StandardCharsets.UTF_8)));

}
