/*
 * Copyright 2023-2025 the original author or authors.
 * <p>
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.mammb.code.editor.core.Theme;

/**
 * The application.
 * @author Naotsugu Kobayashi
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        var ctx = new AppContext(this);
        if (ctx.config().windowPositionX() >= 0 && ctx.config().windowPositionY() >= 0) {
            stage.setX(ctx.config().windowPositionX());
            stage.setY(ctx.config().windowPositionY());
        }
        var appPane = new AppPane(stage, paramPath(), ctx);
        Scene scene = new Scene(appPane, ctx.config().windowWidth(), ctx.config().windowHeight());
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle(Version.appName);
        stage.getIcons().add(new Image(
            Objects.requireNonNull(App.class.getResourceAsStream("/icon.png"))));
        syncConfigProperty(stage, scene, ctx);
        stage.show();

    }

    /**
     * Restores the window position and size from the config.
     * @param stage the stage
     * @param scene the scene
     * @param ctx the application context
     */
    private void syncConfigProperty(Stage stage, Scene scene, AppContext ctx) {
        scene.heightProperty().addListener((_, _, h) -> ctx.config().windowHeight(h.doubleValue()));
        scene.widthProperty().addListener((_, _, w) -> ctx.config().windowWidth(w.doubleValue()));
        stage.xProperty().addListener((_, _, x) -> ctx.config().windowPositionX(x.doubleValue()));
        stage.yProperty().addListener((_, _, y) -> ctx.config().windowPositionY(y.doubleValue()));
    }

    /**
     * Get the content path specified as a command line parameter.
     * @return the content path or {@code null}
     */
    private Path paramPath() {
        Parameters params = getParameters();
        if (params.getRaw().isEmpty()) return null;
        Path path = Path.of(params.getRaw().getLast());
        if (!Files.exists(path)) return null;
        if (!Files.isReadable(path)) return null;
        if (!Files.isRegularFile(path)) return null;
        return path;
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
          -fx-default-button: #2F65CA;
        }
        .text-input, .label {
          -fx-font: 14px "Consolas";
        }
        .button {
          -fx-background-color: /*-fx-shadow-highlight-color,  -fx-outer-border, -fx-inner-border, */ -fx-body-color;
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
          -fx-border-color: app-text transparent transparent transparent;
        }
        .tab-pane > .tab-header-area > .headers-region > .tab:selected {
          /* -fx-color: -fx-hover-base; */
          -fx-color: app-base;
          -fx-border-color: app-base;
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

        .tooltip {
          -fx-font: 13px "Consolas";
        }

        .hyperlink,
        .hyperlink:hover,
        .hyperlink:hover:visited {
            -fx-text-fill: -fx-light-text-color;
            -fx-underline: true;
        }

        """
        .replaceAll("app-base", Theme.dark.baseColor()) // TODO theme vs config
        .replaceAll("app-text", Theme.dark.fgColor())
        .replaceAll("app-back", Theme.dark.baseColor())
        .replaceAll("app-accent", Theme.dark.paleHighlightColor())
        .getBytes(StandardCharsets.UTF_8)));

}
