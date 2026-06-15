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

import javafx.application.HostServices;
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
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.SVGPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A high-featured TreeView for displaying and managing file system paths.
 * This component supports multiple roots, file operations (cut, copy, paste, rename, delete),
 * compact directory display, and inline editing.
 * @author Naotsugu Kobayashi
 */
public class PathTreeView extends TreeView<Path> {

    private static final System.Logger log = System.getLogger(PathTreeView.class.getName());

    private final List<Consumer<Path>> selectActions = new ArrayList<>();
    private final BooleanProperty compactFolders = new SimpleBooleanProperty(this, "compactFolders", true);

    /** the currently "cut" item, managed at the TreeView level to avoid static state. */
    private TreeItem<Path> cutItem = null;

    public PathTreeView(Path... roots) {
        super(new TreeItem<>());
        setShowRoot(false);
        setEditable(true);
        for (Path root : roots) addRoot(root);

        setCellFactory(_ -> new PathTreeCell(this));
        getSelectionModel().selectedItemProperty().addListener(
            (_, _, item) -> {
                if (item != null && item.getValue() != null && !selectActions.isEmpty()) {
                    selectActions.forEach(action -> action.accept(item.getValue()));
                }
            });
        compactFolders.addListener((_, _, _) -> refreshAllRoots());

        // allow adding new roots by dropping directories onto the TreeView's empty space
        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                db.getFiles().stream()
                    .map(File::toPath)
                    .filter(Files::isDirectory)
                    .forEach(this::addRoot);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
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
        getRoot().getChildren().add(item);
        getRoot().getChildren().sort(Comparator.comparing(t -> t.getValue().getFileName().toString()));
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
            if (!Files.isDirectory(getValue())) return;

            try (Stream<Path> stream = Files.list(getValue())) {
                stream.sorted(Comparator.comparing(p -> p.getFileName().toString()))
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
                    if (children.size() == 1 && Files.isDirectory(children.getFirst())) {
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

        /** Recursively stores the expansion state of this node and its children. */
        void storeExpansionState(Map<Path, Boolean> states) {
            if (!isLeaf()) {
                states.put(getValue(), isExpanded());
                for (TreeItem<Path> child : getChildren()) {
                    if (child instanceof PathTreeItem pathChild) {
                        pathChild.storeExpansionState(states);
                    }
                }
            }
        }

        /** Recursively restores the expansion state of this node and its children. */
        void restoreExpansionState(Map<Path, Boolean> states) {
            if (!isLeaf()) {
                // restore state, defaulting to false if not found
                setExpanded(states.getOrDefault(getValue(), false));
                for (TreeItem<Path> child : getChildren()) {
                    if (child instanceof PathTreeItem pathChild) {
                        pathChild.restoreExpansionState(states);
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

        private TextField textField;
        private final FileOperationHandler fileOperationHandler;

        public PathTreeCell(PathTreeView treeView) {
            this.fileOperationHandler = new FileOperationHandler(treeView);
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (getItem() == null) return;

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
            if (getTreeItem() != null && getTreeItem() == ((PathTreeView) getTreeView()).getCutItem()) {
                getStyleClass().add("cut");
            }

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getItem().getFileName().toString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getTreeItem() instanceof CompactPathTreeItem c ? c.getDisplayPath() : item.getFileName().toString());
                    setGraphic(Files.isDirectory(item) ? folder() : file());
                    setContextMenu(buildContextMenu());
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
            ContextMenu menu = new ContextMenu();
            TreeItem<Path> treeItem = getTreeItem();
            boolean isDirectory = Files.isDirectory(getItem());
            boolean isRoot = treeItem.getParent() == getTreeView().getRoot();

            if (isRoot) {
                menu.getItems().add(createMenuItem("Remove", () -> fileOperationHandler.removeRoot(treeItem)));
            } else {
                menu.getItems().addAll(
                    createMenuItem("Cut", () -> fileOperationHandler.cut(treeItem)),
                    createMenuItem("Copy", () -> fileOperationHandler.copy(treeItem)),
                    createMenuItem("Rename", () -> getTreeView().edit(treeItem)),
                    createMenuItem("Delete", () -> fileOperationHandler.delete(treeItem))
                );
            }

            if (isDirectory) {
                MenuItem pasteItem = createMenuItem("Paste", () -> fileOperationHandler.paste(treeItem));
                pasteItem.setDisable(!Clipboard.getSystemClipboard().hasFiles());

                menu.getItems().add(new SeparatorMenuItem());
                menu.getItems().addAll(
                    createMenuItem("New File", () -> fileOperationHandler.createNew(treeItem, true)),
                    createMenuItem("New Directory", () -> fileOperationHandler.createNew(treeItem, false)),
                    pasteItem
                );
                menu.getItems().add(new SeparatorMenuItem());
                menu.getItems().add(createMenuItem("Refresh", () -> fileOperationHandler.refresh(treeItem)));
                if (treeItem instanceof CompactPathTreeItem) {
                    menu.getItems().add(createMenuItem("Expand Directory", () -> fileOperationHandler.expandCompactDirectory((CompactPathTreeItem) treeItem)));
                }
            }

            return menu;
        }

        private MenuItem createMenuItem(String text, Runnable action) {
            MenuItem item = new MenuItem(text);
            item.setOnAction(_ -> action.run());
            return item;
        }
    }

    /**
     * Handles all file system operations, separating logic from the UI (PathTreeCell).
     */
    private static class FileOperationHandler {
        private final PathTreeView treeView;
        private static final DataFormat DATA_FORMAT_CUT = new DataFormat("app/cut-operation");

        FileOperationHandler(PathTreeView treeView) {
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

            Path newPath = findUniquePath(parentPath, isFile ? "Untitled.txt" : "Untitled");

            try {
                if (isFile) {
                    Files.createFile(newPath);
                } else {
                    Files.createDirectory(newPath);
                }

                PathTreeItem newItem = new PathTreeItem(newPath, treeView.isCompactFolders());
                parentItem.getChildren().add(newItem);
                parentItem.getChildren().sort(Comparator.comparing(t -> t.getValue().getFileName().toString()));

                Platform.runLater(() -> {
                    treeView.getSelectionModel().select(newItem);
                    treeView.edit(newItem);
                });

            } catch (IOException e) {
                showError("Creation Failed", "Could not create " + newPath.getFileName() + ": " + e.getMessage());
            }
        }

        void delete(TreeItem<Path> item) {
            Path path = item.getValue();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Are you sure you want to delete " + path.getFileName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
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
            parent.getChildren().sort(Comparator.comparing(t -> t.getValue().getFileName().toString()));
        }

        void removeRoot(TreeItem<Path> item) {
            treeView.getRoot().getChildren().remove(item);
        }

        private Path findUniquePath(Path parent, String baseName) {
            Path newPath = parent.resolve(baseName);
            if (!Files.exists(newPath)) {
                return newPath;
            }
            int i = 1;
            while (true) {
                String numberedName = baseName + " (" + i + ")";
                newPath = parent.resolve(numberedName);
                if (!Files.exists(newPath)) {
                    return newPath;
                }
                i++;
            }
        }

        private void showError(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
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
