/*
 * Copyright 2023-2025 the original author or authors.
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

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.SVGPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A high-featured TreeView for displaying and managing file system paths.
 * This component supports multiple roots, file operations (cut, copy, paste, rename, delete),
 * compact directory display, and inline editing.
 * @author Naotsugu Kobayashi
 */
public class PathTree extends TreeView<Path> {

    /** The logger. */
    private static final System.Logger log = System.getLogger(PathTree.class.getName());

    private final List<Consumer<Path>> selectActions = new ArrayList<>();
    private final List<BiConsumer<Path, Boolean>> doubleSelectActions = new ArrayList<>();
    private final BooleanProperty compactFolders = new SimpleBooleanProperty(this, "compactFolders", true);

    private boolean cellEditable = false;
    /** The currently "cut" item, managed at the TreeView level to avoid static state. */
    private TreeItem<Path> cutItem = null;

    public PathTree(Path... roots) {
        super(new TreeItem<>());
        setShowRoot(false);
        setEditable(true);
        Arrays.stream(roots).forEach(this::addRoot);

        var fileOperationHandler = new FileOperationHandler(this);
        setCellFactory(_ -> new PathTreeCell(this, fileOperationHandler));
        getSelectionModel().selectedItemProperty().addListener((_, _, item) -> {
                if (item != null && item.getValue() != null && Files.isReadable(item.getValue())) {
                    selectActions.forEach(action -> action.accept(item.getValue()));
                }
            });
        compactFolders.addListener((_, _, _) -> refreshAllRoots());

        // allow adding new roots by dropping directories onto the TreeView's empty space
        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        });
        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                db.getFiles().stream()
                    .map(File::toPath)
                    .filter(Files::isDirectory)
                    .forEach(this::addRoot);
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    /**
     * Adds a new root path to the TreeView, avoiding duplicates or subdirectories of existing roots.
     * @param path The path to add as a new root.
     */
    public void addRoot(Path path) {
        List<TreeItem<Path>> existingRoots = new ArrayList<>(getRoot().getChildren());
        for (TreeItem<Path> item : existingRoots) {
            Path existingPath = item.getValue();
            if (path.startsWith(existingPath)) {
                // the new path is a subdirectory of an existing root, so ignore it
                return;
            }
            if (existingPath.startsWith(path)) {
                // an existing root is a subdirectory of the new path, so remove it
                getRoot().getChildren().remove(item);
            }
        }
        PathTreeItem item = new PathTreeItem(path, isCompactFolders());
        item.setExpanded(true);
        getRoot().getChildren().add(item);
        getRoot().getChildren().sort(Comparator
            .comparing((TreeItem<Path> p) -> !Files.isDirectory(p.getValue()))
            .thenComparing(t -> t.getValue().getFileName().toString()));
    }

    public List<Path> rootPaths() {
        return getRoot().getChildren().stream().map(TreeItem::getValue).toList();
    }

    /**
     * Refreshes all root nodes while preserving the expansion state of the tree.
     */
    public void refreshAllRoots() {
        // store the current expansion state of all nodes
        Map<Path, Boolean> expansionStates = new HashMap<>();
        for (TreeItem<Path> item : getRoot().getChildren()) {
            if (item instanceof PathTreeItem pathItem) {
                pathItem.storeExpansionState(expansionStates);
            }
        }
        // refresh the content of all root nodes
        for (TreeItem<Path> item : getRoot().getChildren()) {
            if (item instanceof PathTreeItem pathItem) {
                pathItem.refresh(isCompactFolders());
            }
        }
        // restore the expansion state
        for (TreeItem<Path> item : getRoot().getChildren()) {
            if (item instanceof PathTreeItem pathItem) {
                pathItem.restoreExpansionState(expansionStates);
            }
        }
    }

    public boolean isCompactFolders() {
        return compactFolders.get();
    }

    public BooleanProperty compactFoldersProperty() {
        return compactFolders;
    }

    public void setCompactFolders(boolean compactFolders) {
        this.compactFolders.set(compactFolders);
    }

    public void addSelectAction(Consumer<Path> action) {
        selectActions.add(action);
    }

    public void addDoubleSelectAction(BiConsumer<Path, Boolean> action) {
        doubleSelectActions.add(action);
    }

    public TreeItem<Path> getCutItem() {
        return cutItem;
    }

    public void setCutItem(TreeItem<Path> cutItem) {
        this.cutItem = cutItem;
    }

    // ------------------------------------------------------------------------

    /**
     * A TreeItem that represents a Path and loads its children on demand.
     */
    static class PathTreeItem extends TreeItem<Path> {

        private boolean loaded = false;
        private boolean compact;

        public PathTreeItem(Path value, boolean compact) {
            super(value);
            this.compact = compact;
        }

        @Override
        public boolean isLeaf() {
            return !Files.isDirectory(getValue());
        }

        @Override
        public ObservableList<TreeItem<Path>> getChildren() {
            if (!loaded) {
                loaded = true;
                buildChildren();
            }
            return super.getChildren();
        }

        /**
         * Clears and rebuilds the children of this item.
         * @param newCompact The new compact folders setting.
         */
        public void refresh(boolean newCompact) {
            this.compact = newCompact;
            if (loaded) {
                super.getChildren().clear();
                buildChildren();
            }
        }

        private void buildChildren() {
            if (!Files.isDirectory(getValue()) || !Files.isReadable(getValue())) return;

            try (Stream<Path> stream = Files.list(getValue())) {
                stream.filter(path -> {
                        var fileName = path.getFileName().toString();
                        return !Objects.equals(fileName, ".DS_Store") && !Objects.equals(fileName, "Thumbs.db");
                    })
                    .sorted(Comparator
                        .comparing((Path p) -> !Files.isDirectory(p))
                        .thenComparing((p -> p.getFileName().toString())))
                    .forEach(path -> {
                        if (Files.isDirectory(path) && compact) {
                            super.getChildren().add(buildCompactTreeItem(path));
                        } else {
                            super.getChildren().add(new PathTreeItem(path, compact));
                        }
                    });
            } catch (IOException e) {
                log.log(System.Logger.Level.ERROR, e);
            }
        }

        private TreeItem<Path> buildCompactTreeItem(final Path path) {
            List<Path> chain = new ArrayList<>();
            chain.add(path);
            Path current = path;

            while (true) {
                try (Stream<Path> stream = Files.list(current)) {
                    List<Path> children = stream.toList();
                    if (children.size() == 1 && Files.isDirectory(children.getFirst()) &&
                            Files.isReadable(children.getFirst())) {
                        current = children.getFirst();
                        chain.add(current);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
            }

            if (chain.size() > 1) {
                String displayPath = chain.stream()
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.joining("/"));
                return new CompactPathTreeItem(current, displayPath, compact, path);
            } else {
                return new PathTreeItem(path, compact);
            }
        }

        /** Recursively stores the expansion state of this node and its children.
         *  Uses super.getChildren() to avoid triggering lazy loading of unvisited nodes. */
        void storeExpansionState(Map<Path, Boolean> states) {
            if (!isLeaf()) {
                states.put(getValue(), isExpanded());
                if (loaded && isExpanded()) {
                    for (TreeItem<Path> child : super.getChildren()) {
                        if (child instanceof PathTreeItem pathChild) {
                            pathChild.storeExpansionState(states);
                        }
                    }
                }
            }
        }

        /** Recursively restores the expansion state of this node and its children. */
        void restoreExpansionState(Map<Path, Boolean> states) {
            if (!isLeaf()) {
                // restore state, defaulting to false if not found
                boolean shouldExpand = states.getOrDefault(getValue(), false);
                setExpanded(shouldExpand);
                if (shouldExpand) {
                    for (TreeItem<Path> child : getChildren()) {
                        if (child instanceof PathTreeItem pathChild) {
                            pathChild.restoreExpansionState(states);
                        }
                    }
                }
            }
        }
    }

    /**
     * A specialized TreeItem for representing a chain of single-child directories.
     */
    static class CompactPathTreeItem extends PathTreeItem {
        private final String displayPath;
        private final Path startPath;

        public CompactPathTreeItem(Path value, String displayPath, boolean compact, Path startPath) {
            super(value, compact);
            this.displayPath = displayPath;
            this.startPath = startPath;
        }

        public String getDisplayPath() {
            return displayPath;
        }

        public Path getStartPath() {
            return startPath;
        }
    }


    /**
     * The TreeCell responsible for rendering a Path and handling UI events.
     * It delegates all file system operations to a FileOperationHandler.
     */
    static class PathTreeCell extends TreeCell<Path> {

        private final PathTree treeView;
        private final FileOperationHandler fileOperationHandler;
        private TextField textField;

        private PathTreeCell(PathTree treeView, FileOperationHandler fileOperationHandler) {
            this.treeView = treeView;
            this.fileOperationHandler = fileOperationHandler;
            setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY &&
                        !isEmpty() && getTreeItem() != null) {
                    Path path = getTreeItem().getValue();
                    if (Files.isRegularFile(path) && Files.isReadable(path)) {
                        treeView.doubleSelectActions.forEach(action -> action.accept(path, e.isShortcutDown()));
                        e.consume();
                    }
                }
            });
        }

        @Override
        public void startEdit() {
            if (!treeView.cellEditable || getItem() == null || !Files.isReadable(getItem())) return;
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            String name = getItem().getFileName().toString();
            textField.setText(name);
            setText(null);
            setGraphic(textField);
            textField.requestFocus();

            Platform.runLater(() -> {
                int dotIndex = name.lastIndexOf('.');
                if (Files.isRegularFile(getItem()) && dotIndex > 0) {
                    // for files, select only the name without the extension. For directories, select all
                    textField.selectRange(0, dotIndex);
                } else {
                    textField.selectAll();
                }
            });
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getTreeItem() instanceof CompactPathTreeItem c
                ? c.getDisplayPath()
                : getItem().getFileName().toString());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        protected void updateItem(Path item, boolean empty) {
            super.updateItem(item, empty);

            // apply or remove the 'cut' style class based on the global cut state
            getStyleClass().remove("cut");
            if (getTreeItem() != null && getTreeItem() == treeView.getCutItem()) {
                getStyleClass().add("cut");
            }

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
                setDisable(false);
            } else {
                boolean readable = Files.isReadable(item);
                setDisable(!readable);
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getItem().getFileName().toString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getTreeItem() instanceof CompactPathTreeItem c
                        ? c.getDisplayPath()
                        : item.getFileName().toString());
                    setGraphic(Files.isDirectory(item) ? folder() : file());
                    setContextMenu(readable ? buildContextMenu() : null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField();
            textField.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    fileOperationHandler.rename(getTreeItem(), textField.getText());
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        private ContextMenu buildContextMenu() {
            var menu = new FxContextMenu(true);
            TreeItem<Path> treeItem = getTreeItem();
            boolean isDirectory = Files.isDirectory(getItem());
            boolean isRoot = treeItem.getParent() == treeView.getRoot();

            if (isRoot) {
                Path parent = getItem().getParent();
                if (parent != null) {
                    var move = new MenuItem("Move to Parent Directory");
                    move.setOnAction(_ -> fileOperationHandler.moveToParent(treeItem));
                    menu.getItems().add(move);
                }
                var remove = new MenuItem("Remove");
                remove.setOnAction(_ -> fileOperationHandler.removeRoot(treeItem));
                menu.getItems().add(remove);
            } else {
                var cut = new MenuItem("Cut");
                cut.setOnAction(_ -> fileOperationHandler.cut(treeItem));
                menu.getItems().add(cut);

                var copy = new MenuItem("Copy");
                copy.setOnAction(_ -> fileOperationHandler.copy(treeItem));
                menu.getItems().add(copy);

                var rename = new MenuItem("Rename");
                rename.setOnAction(_ -> withCellEdit(() -> treeView.edit(treeItem)));
                menu.getItems().add(rename);

                var delete = new MenuItem("Delete");
                delete.setOnAction(_ -> handleDelete(treeItem));
                menu.getItems().add(delete);
            }

            if (isDirectory) {
                if (!isRoot) {
                    var asRoot = new MenuItem("Set as Root");
                    asRoot.setOnAction(_ -> fileOperationHandler.setAsRoot(treeItem));
                    menu.getItems().add(asRoot);
                }

                var paste = new MenuItem("Paste");
                paste.setDisable(!Clipboard.getSystemClipboard().hasFiles());
                paste.setOnAction(_ -> fileOperationHandler.paste(treeItem));
                menu.getItems().add(paste);

                menu.getItems().add(new SeparatorMenuItem());

                var newFile = new MenuItem("New File");
                newFile.setOnAction(_ -> fileOperationHandler.createNew(treeItem, true));
                menu.getItems().add(newFile);

                var newDir = new MenuItem("New Directory");
                newDir.setOnAction(_ -> fileOperationHandler.createNew(treeItem, false));
                menu.getItems().add(newDir);

                menu.getItems().add(new SeparatorMenuItem());

                var refresh = new MenuItem("Refresh");
                refresh.setOnAction(_ -> fileOperationHandler.refresh(treeItem));
                menu.getItems().add(refresh);

                if (treeItem instanceof CompactPathTreeItem) {
                    var expand = new MenuItem("Expand Directory");
                    expand.setOnAction(_ -> fileOperationHandler.expandCompactDirectory((CompactPathTreeItem) treeItem));
                    menu.getItems().add(expand);
                }
            } else {
                var open = new MenuItem("Open");
                open.setOnAction(_ ->
                    treeView.doubleSelectActions.forEach(action -> action.accept(getItem(), true)));
                menu.getItems().add(open);
            }

            menu.getItems().add(new SeparatorMenuItem());

            var copyName = new MenuItem("Copy Name");
            copyName.setOnAction(_ -> Clipboard.getSystemClipboard().setContent(
                    Map.of(DataFormat.PLAIN_TEXT, getItem().getFileName().toString())));
            menu.getItems().add(copyName);

            var copyPath = new MenuItem("Copy Path");
            copyPath.setOnAction(_ ->Clipboard.getSystemClipboard().setContent(
                    Map.of(DataFormat.PLAIN_TEXT, getItem().toAbsolutePath().toString())));
            menu.getItems().add(copyPath);

            return menu;
        }

        private void handleDelete(TreeItem<Path> treeItem) {
            Path path = treeItem.getValue();
            var ret = FxDialog.confirmation(treeView.getScene().getWindow(),
                "Are you sure you want to delete " + path.getFileName() + "?")
                .showAndWait().orElse(null);
            if (ret == null || ret != ButtonType.OK) {
                return;
            }
            fileOperationHandler.delete(treeItem);
        }

        private void withCellEdit(Runnable runnable) {
            try {
                treeView.cellEditable = true;
                runnable.run();;
            } finally {
                treeView.cellEditable = false;
            }
        }

    }

    /**
     * Handles all file system operations, separating logic from the UI (PathTreeCell).
     */
    static class FileOperationHandler {
        private final PathTree treeView;
        private static final DataFormat DATA_FORMAT_CUT = new DataFormat("app/cut-operation");

        FileOperationHandler(PathTree treeView) {
            this.treeView = treeView;
        }

        void rename(TreeItem<Path> item, String newName) {
            try {
                Path newPath = item.getValue().resolveSibling(newName);
                Files.move(item.getValue(), newPath);
                item.setValue(newPath); // this is safe as commitEdit is not called here
            } catch (IOException e) {
                showError("Rename Failed", "Could not rename: " + e.getMessage());
            }
        }

        void createNew(TreeItem<Path> parentItem, boolean isFile) {
            Path parentPath = parentItem.getValue();
            if (!Files.isDirectory(parentPath)) return;

            String defaultName = isFile ? "Untitled.md" : "Untitled";
            TextInputDialog dialog = new TextInputDialog(defaultName);
            dialog.initOwner(treeView.getScene().getWindow());
            dialog.setTitle(isFile ? "New File" : "New Directory");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText(isFile ? "File name:" : "Directory name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return;

            String name = result.get().trim();
            if (name.isEmpty()) return;

            Path newPath = parentPath.resolve(name);
            if (Files.exists(newPath)) {
                showError("Already Exists", "A file or directory with that name already exists.");
                return;
            }

            try {
                if (isFile) {
                    Files.createFile(newPath);
                } else {
                    Files.createDirectory(newPath);
                }

                PathTreeItem newItem = new PathTreeItem(newPath, treeView.isCompactFolders());
                parentItem.getChildren().add(newItem);
                parentItem.getChildren().sort(Comparator
                    .comparing((TreeItem<Path> p) -> !Files.isDirectory(p.getValue()))
                    .thenComparing(t -> t.getValue().getFileName().toString()));

                Platform.runLater(() -> treeView.getSelectionModel().select(newItem));

            } catch (IOException e) {
                showError("Creation Failed", "Could not create " + newPath.getFileName() + ": " + e.getMessage());
            }
        }

        void delete(TreeItem<Path> item) {
            Path path = item.getValue();
            try {
                if (Files.isDirectory(path)) {
                    try (Stream<Path> walk = Files.walk(path)) {
                        walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                            try { Files.delete(p); } catch (IOException e) { /* ignore */ }
                        });
                    }
                } else {
                    Files.delete(path);
                }
                item.getParent().getChildren().remove(item);
            } catch (IOException e) {
                showError("Delete Failed", "Could not delete " + path.getFileName() + ": " + e.getMessage());
            }
        }

        void cut(TreeItem<Path> item) {
            if (treeView.getCutItem() != null) {
                treeView.refresh();
            }
            treeView.setCutItem(item);
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.FILES, List.of(item.getValue().toFile()));
            content.put(DATA_FORMAT_CUT, true);
            Clipboard.getSystemClipboard().setContent(content);
            // refresh to apply the "cut" style
            treeView.refresh();
        }

        void copy(TreeItem<Path> item) {
            if (treeView.getCutItem() != null) {
                treeView.refresh();
            }
            treeView.setCutItem(null);
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.FILES, List.of(item.getValue().toFile()));
            Clipboard.getSystemClipboard().setContent(content);
        }

        void paste(TreeItem<Path> targetItem) {
            Path targetDir = targetItem.getValue();
            if (!Files.isDirectory(targetDir)) return;

            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (!clipboard.hasFiles()) return;

            boolean isCut = clipboard.hasContent(DATA_FORMAT_CUT);

            for (File file : clipboard.getFiles()) {
                try {
                    Path sourcePath = file.toPath();
                    Path destPath = targetDir.resolve(sourcePath.getFileName());
                    if (isCut) {
                        Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    showError("Paste Failed", "Could not paste " + file.getName() + ": " + e.getMessage());
                }
            }

            if (isCut) {
                Clipboard.getSystemClipboard().clear();
            }
            treeView.setCutItem(null);
            treeView.refreshAllRoots();
        }

        void refresh(TreeItem<Path> item) {
            if (item instanceof PathTreeItem pathItem) {
                pathItem.refresh(treeView.isCompactFolders());
            }
        }

        void expandCompactDirectory(CompactPathTreeItem compactItem) {
            TreeItem<Path> parent = compactItem.getParent();
            if (parent == null) return;

            int index = parent.getChildren().indexOf(compactItem);
            parent.getChildren().remove(index);

            Path startPath = compactItem.getStartPath();
            Path endPath = compactItem.getValue();

            List<Path> pathSegments = new ArrayList<>();
            Path temp = endPath;
            while (temp != null && !temp.equals(startPath.getParent())) {
                pathSegments.addFirst(temp);
                if (temp.equals(startPath)) break;
                temp = temp.getParent();
            }

            TreeItem<Path> currentInsertionPoint = parent;
            for (int i = 0; i < pathSegments.size(); i++) {
                Path segment = pathSegments.get(i);
                PathTreeItem newSegmentItem = new PathTreeItem(segment, false);
                newSegmentItem.setExpanded(i < pathSegments.size() - 1);

                Optional<TreeItem<Path>> existing = currentInsertionPoint.getChildren().stream()
                    .filter(child -> child.getValue().equals(segment))
                    .findFirst();

                if (existing.isPresent()) {
                    currentInsertionPoint = existing.get();
                } else {
                    if (currentInsertionPoint == parent) {
                        parent.getChildren().add(index, newSegmentItem);
                    } else {
                        currentInsertionPoint.getChildren().add(newSegmentItem);
                    }
                    currentInsertionPoint = newSegmentItem;
                }
            }
            parent.getChildren().sort(Comparator
                .comparing((TreeItem<Path> p) -> !Files.isDirectory(p.getValue()))
                .thenComparing(t -> t.getValue().getFileName().toString()));
        }

        void removeRoot(TreeItem<Path> item) {
            treeView.getRoot().getChildren().remove(item);
        }

        void moveToParent(TreeItem<Path> item) {
            Path currentPath = item.getValue();
            Path parentPath = currentPath.getParent();
            if (parentPath == null) return;
            changeRootPath(item, parentPath, currentPath);
        }

        void setAsRoot(TreeItem<Path> item) {
            Path targetPath = item.getValue();
            TreeItem<Path> rootItem = item;
            while (rootItem != null && rootItem.getParent() != treeView.getRoot()) {
                rootItem = rootItem.getParent();
            }
            if (rootItem == null) return;
            changeRootPath(rootItem, targetPath, targetPath);
        }

        private void changeRootPath(TreeItem<Path> rootItem, Path targetPath, Path selectPath) {
            // store the expansion state of the current root and its children
            Map<Path, Boolean> expansionStates = new HashMap<>();
            if (rootItem instanceof PathTreeItem pathItem) {
                pathItem.storeExpansionState(expansionStates);
            }
            expansionStates.put(rootItem.getValue(), rootItem.isExpanded());
            expansionStates.put(targetPath, true); // Keep the target directory expanded

            // check for duplicate or overlapping roots
            List<TreeItem<Path>> otherRoots = new ArrayList<>(treeView.getRoot().getChildren());
            otherRoots.remove(rootItem);

            boolean duplicateOrSub = false;
            List<TreeItem<Path>> toRemove = new ArrayList<>();
            for (TreeItem<Path> other : otherRoots) {
                Path existingPath = other.getValue();
                if (targetPath.startsWith(existingPath)) {
                    // the new target directory is already inside an existing root
                    duplicateOrSub = true;
                } else if (existingPath.startsWith(targetPath)) {
                    // an existing root is a subdirectory of the new target directory
                    toRemove.add(other);
                    if (other instanceof PathTreeItem pathItem) {
                        pathItem.storeExpansionState(expansionStates);
                    }
                    expansionStates.put(existingPath, other.isExpanded());
                }
            }

            if (duplicateOrSub) {
                // remove the current root and restore expansion states on the containing root
                treeView.getRoot().getChildren().remove(rootItem);
                for (TreeItem<Path> other : treeView.getRoot().getChildren()) {
                    if (other instanceof PathTreeItem pathItem && targetPath.startsWith(other.getValue())) {
                        pathItem.restoreExpansionState(expansionStates);
                    }
                }
                return;
            }

            // remove nested roots since they will be covered by the target directory
            treeView.getRoot().getChildren().removeAll(toRemove);

            // replace the current root item with the target directory item
            PathTreeItem newItem = new PathTreeItem(targetPath, treeView.isCompactFolders());
            newItem.setExpanded(true); // Expand the new parent by default

            int index = treeView.getRoot().getChildren().indexOf(rootItem);
            if (index >= 0) {
                treeView.getRoot().getChildren().set(index, newItem);
            } else {
                treeView.getRoot().getChildren().add(newItem);
            }

            // sort roots
            treeView.getRoot().getChildren().sort(Comparator
                .comparing((TreeItem<Path> p) -> !Files.isDirectory(p.getValue()))
                .thenComparing(t -> t.getValue().getFileName().toString()));

            // restore expansion states
            newItem.restoreExpansionState(expansionStates);

            // select the target directory node under the new parent, or the new parent if not found
            treeView.getSelectionModel().clearSelection();
            TreeItem<Path> originalItemInNewRoot = newItem.getChildren().stream()
                .filter(child -> child.getValue().equals(selectPath))
                .findFirst()
                .orElse(newItem);
            treeView.getSelectionModel().select(originalItemInNewRoot);
        }

        private void showError(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(treeView.getScene().getWindow());
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    // --- Static helper methods for graphics ---

    static SVGPath folder() {
        return svg("""
            M1 3.5A1.5 1.5 0 0 1 2.5 2h2.764c.958 0 1.76.56 2.311 1.184C7.985 3.648 8.48 4 9 4h4.5A1.5 1.5 0 0 1 15 5.5v7a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 1 12.5zM2.5 3a.5.5 0 0 0-.5.5V6h12v-.5a.5.5 0 0 0-.5-.5H9c-.964 0-1.71-.629-2.174-1.154C6.374 3.334 5.82 3 5.264 3zM14 7H2v5.5a.5.5 0 0 0 .5.5h11a.5.5 0 0 0 .5-.5z
            """);
    }
    static SVGPath file() {
        return svg("""
            M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5zm-3 0A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V4.5z
            """);
    }

    private static SVGPath svg(String d) {
        var svg = new SVGPath();
        svg.setContent(d);
        svg.getStyleClass().add("icon");
        svg.setStyle("-fx-fill: -fx-light-text-color;");
        return svg;
    }

}
