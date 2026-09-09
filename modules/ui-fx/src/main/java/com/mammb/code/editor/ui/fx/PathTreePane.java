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

import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.Session;
import com.mammb.code.jfx.tabcontainer.ContentPane;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import java.nio.file.Path;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The PathTreePane.
 * @author Naotsugu Kobayashi
 */
public class PathTreePane extends ContentPane {

    /** The logger. */
    private static final System.Logger log = System.getLogger(PathTreePane.class.getName());

    /** The context. */
    private final FxAppContext ctx;
    /** The pathTreeView. */
    private final PathTree pathTree;
    /** The short name property. */
    private final SimpleObjectProperty<String> shortNameProperty = new SimpleObjectProperty<>("File Tree");
    /** The full name property. */
    private final SimpleObjectProperty<String> fullNameProperty = new SimpleObjectProperty<>("File Tree");

    /**
     * Constructor.
     * @param ctx the context
     * @param roots the root path
     */
    public PathTreePane(FxAppContext ctx, Path... roots) {
        this.ctx = ctx;
        this.pathTree = new PathTree(roots);
        this.pathTree.addDoubleSelectAction(this::handleDoubleSelectAction);
        this.pathTree.setDeleteApplyAction(this::handleItemDeleteAction);
        this.pathTree.setRenameApplyAction(this::handleItemRenameAction);
        setPrefWidth(200);
        getChildren().add(pathTree);
    }

    private void handleDoubleSelectAction(Path path, boolean isShortcutDown) {
        if (Files.isReadableFile(path)) {
            List<EditorPane> panes = ctx.container()
                .find(p -> p instanceof EditorPane).stream()
                .map(EditorPane.class::cast).toList();
            if (panes.isEmpty()) {
                ctx.container().add(new EditorPane(ctx).bindLater(Session.of(path)));
            }

            var found = panes.stream()
                .filter(p -> Objects.equals(p.query(Query.contentPath).orElse(null), path))
                .findFirst();

            if (found.isPresent()) {
                ctx.container().select(found.get());
            } else {
                panes.getFirst().open(path, isShortcutDown);
            }
        }
    }

    private boolean handleItemDeleteAction(TreeItem<Path> item, Consumer<TreeItem<Path>> consumer) {
        Path path = item.getValue();
        if (Files.isReadableFile(path)) {
            var panes = ctx.container().find(EditorPane.class)
                .filter(pane -> Objects.equals(pane.query(Query.contentPath).orElse(null), path))
                .toList();
            panes.forEach(pane -> ctx.container().closeForce(pane));
        } else if (Files.isReadableDirectory(path)) {
            var panes = ctx.container().find(EditorPane.class)
                .filter(pane -> {
                    Path p = pane.query(Query.contentPath).orElse(null);
                    return (p != null && p.startsWith(path));
                })
                .toList();
            panes.forEach(pane -> ctx.container().closeForce(pane));
        } else {
            return false;
        }
        consumer.accept(item);
        return true;
    }

    private boolean handleItemRenameAction(TreeItem<Path> item, String name, BiConsumer<TreeItem<Path>, String> consumer) {
        Path path = item.getValue();
        if (Files.isReadableFile(path)) {
            var panes = ctx.container().find(EditorPane.class)
                .filter(pane -> Objects.equals(pane.query(Query.contentPath).orElse(null), path))
                .toList();
            if (!panes.stream().allMatch(EditorPane::closeRequest)) return false;
            panes.forEach(EditorPane::close);
            consumer.accept(item, name);
            Path newPath = path.getParent().resolve(Path.of(name));
            panes.forEach(pane -> pane.openOn(Session.of(newPath)));
        } else if (Files.isReadableDirectory(path)) {
            var panes = ctx.container().find(EditorPane.class)
                .filter(pane -> {
                    Path p = pane.query(Query.contentPath).orElse(null);
                    return (p != null && p.startsWith(path));
                })
                .toList();
            List<Path> oldPaths = panes.stream().map(pane -> pane.query(Query.contentPath).orElse(null)).toList();
            Queue<Path> newPaths = oldPaths.stream().map(old -> {
                Path sub = path.relativize(old);
                Path newDir = path.getParent().resolve(Path.of(name));
                return newDir.resolve(sub);
            }).collect(Collectors.toCollection(ArrayDeque::new));
            if (!panes.stream().allMatch(EditorPane::closeRequest)) return false;
            panes.forEach(EditorPane::close);
            consumer.accept(item, name);
            panes.forEach(pane -> pane.openOn(Session.of(newPaths.poll())));
        } else {
            return false;
        }
        return true;
    }


    public static PathTreePane fromString(FxAppContext ctx, String string) {
        if (string == null || string.isBlank()) return new PathTreePane(ctx);
        if (string.startsWith("PathTreePane[")) {
            string = string.substring("PathTreePane[".length(), string.length() - 1);
        } else if (string.startsWith("[")) {
            string = string.substring(1, string.length() - 1);
        }
        return new PathTreePane(ctx, Arrays.stream(string.split(File.pathSeparator))
            .map(Path::of).toArray(Path[]::new));
    }

    @Override
    public String asString() {
        return "PathTreePane" + pathTree.rootPaths().stream()
            .map(Path::toAbsolutePath).map(Path::toString)
            .collect(Collectors.joining(File.pathSeparator, "[", "]"));
    }

    @Override
    public void focus() {
    }

    @Override
    public boolean canCloseQuiet() {
        return true;
    }

    @Override
    public boolean closeRequest() {
        return true;
    }

    @Override
    public void close() {
    }


    @Override
    public ReadOnlyObjectProperty<String> shortNameProperty() {
        return shortNameProperty;
    }

    @Override
    public ReadOnlyObjectProperty<String> fullNameProperty() {
        return fullNameProperty;
    }

}
