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

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.platform.AppPaths;
import com.mammb.code.editor.ui.base.AppContext;
import com.mammb.code.jfx.tabcontainer.ContainerHandle;
import com.mammb.code.jfx.tabcontainer.ContentPane;
import com.mammb.code.jfx.tabcontainer.TabContainer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    /** The context. */
    private final FxAppContext ctx;

    /**
     * Constructor.
     */
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

        var tabContainer = TabContainer.of(
            this::handleRequireContent,
            this::handleRequestContent,
            this::handleRequireStage,
            this::handleTabMenu
        );
        ctx.container(tabContainer);

        Pane pane = tabContainer.resume(stage,
            AppPaths.applicationConfPath.resolve("resumes"),
            this::handleResume);

        paramPaths().forEach(path ->
            tabContainer.add(new EditorPane(ctx).bindLater(Session.of(path))));

        intiStage(stage, pane);
        stage.show();

    }

    private ContentPane handleRequireContent() {
        return new EditorPane(ctx).bindLater(Session.empty());
    }

    private Stage handleRequireStage(Pane pane) {
        return intiStage(new Stage(), pane);
    }

    private void handleRequestContent(ContainerHandle containerHandle, Path path) {
        if (path == null) {
            return;
        }
        var found = containerHandle.findFirst(contentPane -> {
            if (contentPane instanceof EditorPane editorPane) {
                return editorPane.query(Query.contentPath)
                    .filter(contentPath -> Objects.equals(contentPath, path)).isPresent();
            } else {
                return false;
            }
        });
        if (found.isPresent()) {
            containerHandle.select(found.get());
        } else {
            containerHandle.add(new EditorPane(ctx).bindLater(Session.of(path)));
        }
    }

    private MenuItem[] handleTabMenu(ContentPane contentPane, MenuItem... menuItems) {
        if (contentPane instanceof EditorPane editorPane) {
            var treeView = new MenuItem("Open File Tree");
            treeView.setOnAction(_ -> editorPane.addPathTreeView());
            MenuItem[] items = Arrays.copyOf(menuItems, menuItems.length + 1);
            items[menuItems.length - 1] = new SeparatorMenuItem();
            items[menuItems.length] = treeView;
            return items;
        } else if (contentPane instanceof PathTreePane) {
            return Arrays.copyOf(menuItems, menuItems.length - 3);
        }
        return menuItems;
    }

    private ContentPane handleResume(String str) {
        return (str != null  && str.startsWith("PathTreePane"))
            ? PathTreePane.fromString(ctx, str)
            : new EditorPane(ctx).bindLater(Session.valueOf(str));
    }

    private Stage intiStage(Stage stage, Pane pane) {

        var stackPane = new StackPane();
        var mainPane = new BorderPane(pane);
        var notifyListener = new NotificationPane(stackPane);
        ctx.addNotifyListener(stage, notifyListener);
        stackPane.getChildren().addAll(mainPane, notifyListener);

        Scene scene = new Scene(stackPane, Color.TRANSPARENT);
        AppCss.of(Theme.current).apply(scene);
        stage.setScene(scene);
        stage.setTitle(AppVersion.appName);
        stage.getIcons().add(new Image(
            Objects.requireNonNull(App.class.getResourceAsStream("/icon.png"))));

        bindRefreshOnFocused(stage, ctx.container());
        if (System.getProperty("idleGcDelayMillis") != null) {
            // -DidleGcDelayMillis=3000
            bindGcTimer(stage, ctx, Double.parseDouble(System.getProperty("idleGcDelayMillis")));
        }
        return stage;
    }

    /**
     * Get the content path specified as a command line parameter.
     * @return the content path list
     */
    private List<Path> paramPaths() {
        return getParameters().getUnnamed().stream().map(Path::of)
            .filter(Files::exists).filter(Files::isReadableFile).toList();
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
     * when focus is gained, reload external changes to the content.
     */
    private void bindRefreshOnFocused(Stage stage, ContainerHandle containerHandle) {
        stage.focusedProperty().addListener((_, _, focused) -> {
            if (focused) {
                containerHandle.find(EditorPane.class)
                    .filter(p -> p.getParent().getScene().getWindow() == stage)
                    .forEach(EditorPane::refreshIfNeeded);
            }
        });
    }


    /**
     * Binds a garbage collection timer to the provided stage. The timer triggers
     * garbage collection after the specified delay when the stage loses focus and stops
     * when the stage regains focus.
     *
     * @param stage the stage to which the garbage collection timer is bound
     * @param ctx the context
     * @param delayMillis the delay in milliseconds before garbage collection is triggered
     */
    private static void bindGcTimer(Stage stage, AppContext ctx, double delayMillis) {
        PauseTransition timer = new PauseTransition(Duration.millis(delayMillis));
        timer.setOnFinished(_ ->
            ctx.spawn(() -> {
                if (Stage.getWindows().stream().anyMatch(Window::isFocused)) return;
                if (stage.isFocused()) return;
                long beforeFree = Runtime.getRuntime().freeMemory();
                long beforeTotal = Runtime.getRuntime().totalMemory();
                System.gc();
                long afterFree = Runtime.getRuntime().freeMemory();
                long afterTotal = Runtime.getRuntime().totalMemory();
                log.log(System.Logger.Level.INFO, "GC: {0,number,#,###}/{1,number,#,###} -> {2,number,#,###}/{3,number,#,###}",
                    beforeTotal - beforeFree, beforeTotal,
                    afterTotal - afterFree, afterTotal);
            })
        );
        stage.focusedProperty().addListener((_, _, focused) -> {
            if (focused) {
                timer.stop();
            } else {
                timer.playFromStart();
            }
        });
    }

}
