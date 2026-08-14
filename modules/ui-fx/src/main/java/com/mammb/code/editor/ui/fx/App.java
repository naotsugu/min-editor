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
package com.mammb.code.editor.ui.fx;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.platform.AppPaths;
import com.mammb.code.jfx.multitab.Container;
import com.mammb.code.jfx.multitab.ContentPane;
import com.mammb.code.jfx.multitab.TabContainers;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.sun.javafx.tk.Toolkit;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.platform.AppVersion;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * The App class serves as the entry point for the JavaFX application.
 * It initializes and configures the primary stage, scene, and application context.
 * @author Naotsugu Kobayashi
 */
public class App extends Application {

    /** The logger. */
    private static final System.Logger log = System.getLogger(App.class.getName());

    private final FxAppContext ctx;

    public App() {
        this.ctx = new FxAppContext(this);
    }

    @Override
    public void start(Stage stage) {

        if (System.getProperty("core.theme") == null) {
            System.setProperty("core.theme",
                Platform.getPreferences().getColorScheme().name().toLowerCase());
        }

        // if additional fonts are added, set them as the default font
        ctx.config().defaultFontName(
            loadFonts(AppPaths.applicationHomePath()).orElse(null));

        Scene scene = TabContainers.builder()
            .stage(stage)
            .toContent(this::toContent)
            .toScene(this::toScene)
            .onOpenRequest(this::onOpenRequest)
            .resume(AppPaths.applicationConfPath.resolve("resumes"), this::toContent)
            .build();

        stage.setScene(scene);
        stage.show();

    }

    private ContentPane toContent(Object arg) {
        var session = switch (arg) {
            case Path path -> Session.of(path);
            case String string -> Session.valueOf(string);
            case null, default -> Session.empty();
        };
        return new EditorPane(ctx).bindLater(session);
    }

    private boolean onOpenRequest(Object arg, Container container) {
        var found = container.find(contentPane -> {
            if (contentPane instanceof EditorPane editorPane) {
                return editorPane.query(Query.contentPath)
                    .filter(contentPath -> Objects.equals(contentPath, arg)).isPresent();
            } else {
                return false;
            }
        });
        if (found.isPresent()) {
            container.select(found.get());
            return true;
        } else {
            return false;
        }
    }

    private Scene toScene(Stage stage, Pane tabContainerPane) {

        Scene scene = new Scene(new AppPane(stage, tabContainerPane, ctx), Color.TRANSPARENT);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle(AppVersion.appName);
        stage.getIcons().add(new Image(
            Objects.requireNonNull(App.class.getResourceAsStream("/icon.png"))));

        if (System.getProperty("idleGcDelayMillis") != null) {
            // -DidleGcDelayMillis=3000
            bindGcTimer(stage, Double.parseDouble(System.getProperty("idleGcDelayMillis")));
        }

        return scene;
    }

    /**
     * Get the content path specified as a command line parameter.
     * @return the content path or {@code null}
     */
    private Path paramPath() {
        Parameters params = getParameters();
        if (params.getRaw().isEmpty()) return null;
        Path path = Path.of(params.getRaw().getLast());
        if (!Files.isReadableFile(path)) return null;
        return path;
    }

    /**
     * Load fonts.
     * @param path the application path
     * @return the font name or {@code Optional.empty()}
     */
    private Optional<String> loadFonts(Path path) {
        if (path == null) return Optional.empty();
        if (!Files.exists(path)) return Optional.empty();
        return Files.list(path).filter(Files::isReadableFile)
            .filter(p -> p.getFileName().toString().endsWith(".ttf"))
            .map(p -> {
                try (var is = Files.newInputStream(p)) {
                    return Toolkit.getToolkit().getFontLoader().loadFont(is, 0, false);
                } catch (Exception ignore) {
                    log.log(System.Logger.Level.WARNING, ignore);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(Font::getName)
            .sorted().findFirst();
    }

    /**
     * Binds a garbage collection timer to the provided stage. The timer triggers
     * garbage collection after the specified delay when the stage loses focus and stops
     * when the stage regains focus.
     *
     * @param stage the stage to which the garbage collection timer is bound
     * @param delayMillis the delay in milliseconds before garbage collection is triggered
     */
    public static void bindGcTimer(Stage stage, double delayMillis) {
        PauseTransition timer = new PauseTransition(Duration.millis(delayMillis));
        timer.setOnFinished(_ -> {
            Thread gcThread = new Thread(() -> {
                if (Stage.getWindows().stream().anyMatch(Window::isFocused)) return;
                if (stage.isFocused()) return;
                long beforeTotal = Runtime.getRuntime().totalMemory();
                long beforeFree = Runtime.getRuntime().freeMemory();
                System.gc();
                log.log(System.Logger.Level.INFO, "GC: {0,number,#,###}/{1,number,#,###} -> {2,number,#,###}/{3,number,#,###}",
                    beforeFree, beforeTotal,
                    Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory());
            });
            gcThread.setDaemon(true);
            gcThread.setName("Idle-GC-Thread");
            gcThread.start();
        });
        stage.focusedProperty().addListener((_, _, focused) -> {
            if (focused) {
                timer.stop();
            } else {
                timer.playFromStart();
            }
        });
    }

    /** The app css. */
    private static final String css = String.join(",", "data:text/css;base64",
        Base64.getEncoder().encodeToString("""
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
          -fx-font-family: "Consolas";
          -fx-font-size: 14px;
        }
        .text-input, .label, .tooltip {
          -fx-font-size: 14px;
        }

        .text-input:focused {
          -fx-background-color: -fx-focus-color, -fx-control-inner-background;
        }
        .button {
          -fx-background-color: -fx-body-color;
        }
        .button:hover {
          -fx-text-fill: white;
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
          -fx-background-color :derive(app-text, -50%);
          -fx-background-insets : 1.0, 0.0, 0.0;
        }
        .scroll-bar .thumb:hover {
          -fx-background-color :derive(app-text, -30%);
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

        .tab-pane {
            -fx-tab-min-height: 1.5em;
            -fx-tab-max-height: 1.5em;
        }
        .tab .label {
          -fx-font-size: 0.916667em;
        }
        .tab-pane > .tab-header-area > .headers-region > .tab {
           -fx-background-color: -fx-hover-base;
        }
        .tab-pane > .tab-header-area > .headers-region > .tab:selected {
          -fx-background-color: derive(-fx-box-border, 30%);
          -fx-border-color: derive(app-text, -30%) transparent transparent transparent;
        }
        .tab-pane:focused > .tab-header-area > .headers-region > .tab:selected {
          -fx-border-color: app-text transparent transparent transparent;
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

        .hyperlink,
        .hyperlink:hover,
        .hyperlink:hover:visited {
            -fx-text-fill: -fx-light-text-color;
            -fx-underline: true;
        }

        .context-menu {
            -fx-font-family: "System";
        }
        """
            .replaceAll("app-base", Theme.current.baseColor().web()) // TODO theme vs config
            .replaceAll("app-text", Theme.current.fgColor().web())
            .replaceAll("app-back", Theme.current.baseColor().web())
            .replaceAll("app-accent", Theme.current.paleHighlightColor().web())
            .getBytes(StandardCharsets.UTF_8)));

}
