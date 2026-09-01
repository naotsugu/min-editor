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
import java.nio.file.Path;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
