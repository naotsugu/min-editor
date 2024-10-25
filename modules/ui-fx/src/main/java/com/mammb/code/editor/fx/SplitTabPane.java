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

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SplitTabPane extends StackPane implements Hierarchical<SplitTabPane> {

    private static final AtomicReference<Tab> draggedTab = new AtomicReference<>();
    private static final AtomicReference<DndTabPane> activePane = new AtomicReference<>();
    private static final DataFormat tabMove = new DataFormat("SplitTabPane:tabMove");

    private SplitPane pane = new SplitPane();
    private SplitTabPane parent;

    public SplitTabPane() {
        getChildren().add(pane);
    }
    public SplitTabPane(EditorPane node) {
        this();
        add(node);
    }
    private SplitTabPane(EditorPane node, SplitTabPane parent) {
        this();
        add(node);
        this.parent = parent;
    }
    private SplitTabPane(DndTabPane node, SplitTabPane parent) {
        this();
        pane.getItems().add(node);
        this.parent = parent;
        node.setParent(this);
    }
    private DndTabPane add(EditorPane node) {
        pane.getItems().clear();
        DndTabPane dndTabPane = new DndTabPane(this, node);
        pane.getItems().add(dndTabPane);
        return dndTabPane;
    }
    private void remove(SplitTabPane splitTabPane) {
        pane.getItems().remove(splitTabPane);
        var node = pane.getItems().getFirst();
        if (node instanceof SplitTabPane stp) {
            pane.getItems().clear();
            getChildren().clear();
            pane = stp.pane;
            getChildren().add(pane);
        }
        pane.getItems()
                .forEach(i -> ((Hierarchical) i).setParent(this));
    }
    private DndTabPane addRight(EditorPane node) {
        if (pane.getItems().isEmpty()) {
            return add(node);
        } else {
            var item = (DndTabPane) pane.getItems().getFirst();
            pane.getItems().clear();
            pane.setOrientation(Orientation.HORIZONTAL);
            var left  = new SplitTabPane(item, this);
            var right = new SplitTabPane(node, this);
            pane.getItems().addAll(left, right);
            return (DndTabPane) right.pane.getItems().getFirst();
        }
    }
    private DndTabPane addLeft(EditorPane node) {
        if (pane.getItems().isEmpty()) {
            return add(node);
        } else {
            var item = (DndTabPane) pane.getItems().getFirst();
            pane.getItems().clear();
            pane.setOrientation(Orientation.HORIZONTAL);
            var left  = new SplitTabPane(node, this);
            var right = new SplitTabPane(item, this);
            pane.getItems().addAll(left, right);
            return (DndTabPane) left.pane.getItems().getFirst();
        }
    }
    private DndTabPane addTop(EditorPane node) {
        if (pane.getItems().isEmpty()) {
            return add(node);
        } else {
            var item = (DndTabPane) pane.getItems().getFirst();
            pane.getItems().clear();
            pane.setOrientation(Orientation.VERTICAL);
            var top = new SplitTabPane(node, this);
            var bottom = new SplitTabPane(item, this);
            pane.getItems().addAll(top, bottom);
            return (DndTabPane) top.pane.getItems().getFirst();
        }
    }
    private DndTabPane addBottom(EditorPane node) {
        if (pane.getItems().isEmpty()) {
            return add(node);
        } else {
            var item = (DndTabPane) pane.getItems().getFirst();
            pane.getItems().clear();
            pane.setOrientation(Orientation.VERTICAL);
            var top = new SplitTabPane(item, this);
            var bottom = new SplitTabPane(node, this);
            pane.getItems().addAll(top, bottom);
            return (DndTabPane) bottom.pane.getItems().getFirst();
        }
    }

    @Override
    public void setParent(SplitTabPane parent) {
        this.parent = parent;
    }

    static class DndTabPane extends StackPane implements Hierarchical<SplitTabPane> {
        private final TabPane tabPane = new TabPane();
        private final Rectangle marker = new Rectangle();
        private SplitTabPane parent;
        DndTabPane(SplitTabPane parent, EditorPane node) {
            this.parent = parent;
            getChildren().addAll(tabPane, marker);
            marker.setFill(Color.TRANSPARENT);
            marker.setStroke(Color.DARKORANGE);
            marker.setManaged(false);
            tabPane.focusedProperty().addListener(this::handleFocused);
            setOnDragOver(this::handleDragOver);
            setOnDragDropped(this::handleDragDropped);
            setOnDragExited(this::handleDragExited);
            setOnDragDone(this::handleDragDone);
            add(node);
        }
        void add(EditorPane node) {
            var tab = new Tab();
            tab.setContent(node);
            initTab(tab);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            tabPane.getSelectionModel().selectedItemProperty().addListener(this::handleSelectedTabItem);
        }
        private void initTab(Tab tab) {
            var node = (EditorPane) tab.getContent();
            var label = new Label(node.fileNameProperty().get());
            tab.setGraphic(label);
            label.setOnDragDetected(this::handleTabDragDetected);
            tab.setOnClosed(this::handleOnTabClosed);
            node.fileNameProperty().addListener((ob, o, n) -> label.setText(n));
            node.setNewOpenHandler(path -> add(new EditorPane()));
        }
        private void handleFocused(ObservableValue<? extends Boolean> ob, Boolean o, Boolean focused) {
            if (focused) focus();
        }
        private void focus() {
            var active = activePane.get();
            if (active != null) {
                active.getStyleClass().remove("app-tab-pane-active");
            }
            getStyleClass().add("app-tab-pane-active");
            activePane.set(this);
            if (!tabPane.getTabs().isEmpty()) {
                var tab = tabPane.getSelectionModel().getSelectedItem();
                var editorPane = (EditorPane) tab.getContent();
                editorPane.focus();
            }
        }
        private void handleSelectedTabItem(ObservableValue<? extends Tab> ob, Tab o, Tab tab) {
            if (tab != null) {
                ((EditorPane) tab.getContent()).focus();
            }
        }
        private void handleOnTabClosed(Event e) {
            if (tabPane.getTabs().isEmpty()) {
                if (parent.parent == null) {
                    add(new EditorPane());
                } else {
                    parent.parent.remove(parent);
                }
            }
        }
        private void handleTabDragDetected(MouseEvent e) {
            if (e.getSource() instanceof Label label) {
                Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.put(tabMove, String.valueOf(System.identityHashCode(label)));
                Image image = tabImage(label);
                db.setDragView(image, image.getWidth() / 2, image.getHeight() / 2);
                db.setContent(cc);
                Tab tab = getTabPane().getTabs().stream().filter(t -> t.getGraphic().equals(label)).findFirst().get();
                draggedTab.set(tab);
            }
        }

        private void handleDragOver(DragEvent e) {
            marker.setVisible(false);
            if (e.getDragboard().hasFiles() && dropPoint(this, e) == DropPoint.HEADER) {
                marker.setX(0.0);
                marker.setY(0.0);
                marker.setWidth(tabPane.getLayoutBounds().getWidth());
                marker.setHeight(25);
                marker.setVisible(true);
                e.acceptTransferModes(TransferMode.COPY);
                e.consume();
                return;
            }

            Dragboard db = e.getDragboard();
            Tab dragged = draggedTab.get();
            if (!db.hasContent(tabMove) || dragged == null) return;
            e.acceptTransferModes(TransferMode.MOVE);
            Bounds bounds = tabPane.getLayoutBounds();
            marker.setX(0.0);
            marker.setY(0.0);
            marker.setWidth(bounds.getWidth());
            marker.setHeight(bounds.getHeight());
            marker.setVisible(true);
            switch (dropPoint(this, e)) {
                case LEFT -> marker.setWidth(bounds.getWidth() / 2);
                case TOP -> marker.setHeight(bounds.getHeight() / 2);
                case ANY -> e.acceptTransferModes(TransferMode.NONE);
                case RIGHT -> {
                    marker.setX(bounds.getCenterX());
                    marker.setWidth(bounds.getWidth() / 2);
                }
                case BOTTOM -> {
                    marker.setY(bounds.getCenterY());
                    marker.setHeight(bounds.getHeight() / 2);
                }
                case HEADER -> {
                    int insertionIndex = insertionIndex(e);
                    int tabIndex = Math.min(tabPane.getTabs().size() - 1, insertionIndex);
                    Node tabNode = tabNode(tabPane.getTabs().get(tabIndex).getGraphic());
                    Bounds ins = screenToLocal(tabNode.localToScreen(tabNode.getBoundsInLocal()));
                    marker.setX((insertionIndex > tabIndex) ? ins.getMaxX() : ins.getMinX());
                    marker.setHeight(ins.getHeight());
                    marker.setWidth(2);
                }
            }
            e.consume();
        }
        private void handleDragDropped(DragEvent e) {

            var db = e.getDragboard();
            if (db.hasFiles() && dropPoint(this, e) == DropPoint.HEADER) {
                var path = db.getFiles().stream().map(File::toPath)
                        .filter(Files::isReadable).filter(Files::isRegularFile)
                        .findFirst().orElse(null);
                add(new EditorPane(path));
                e.setDropCompleted(false);
                return;
            }

            Tab dragged = draggedTab.get();
            if (!db.hasContent(tabMove) || dragged == null) {
                e.setDropCompleted(false);
                return;
            }
            marker.setVisible(false);
            DndTabPane from = (DndTabPane) dragged.getTabPane().getParent();
            if (from == this) {
                // drop to self Pane
                if (from.tabPane.getTabs().size() <= 1) {
                    e.setDropCompleted(true);
                    return;
                }
                switch (dropPoint(this, e)) {
                    case HEADER -> {
                        int insertionIndex = insertionIndex(e);
                        int fromIndex = tabPane.getTabs().indexOf(dragged);
                        int toIndex = Math.min(tabPane.getTabs().size() - 1, insertionIndex);
                        if (fromIndex == toIndex) return;
                        tabPane.getTabs().remove(dragged);
                        tabPane.getTabs().add(toIndex, dragged);
                    }
                    case RIGHT -> {
                        from.tabPane.getTabs().remove(dragged);
                        var dndTabPane = parent.addRight((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                    case LEFT -> {
                        from.tabPane.getTabs().remove(dragged);
                        var dndTabPane = parent.addLeft((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                    case TOP -> {
                        from.tabPane.getTabs().remove(dragged);
                        var dndTabPane = parent.addTop((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                    case BOTTOM -> {
                        from.tabPane.getTabs().remove(dragged);
                        var dndTabPane = parent.addBottom((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                }
            } else {
                // drop to another Pane
                boolean unplug = dragged.getTabPane().getTabs().size() <= 1;
                switch (dropPoint(this, e)) {
                    case HEADER -> {
                        int insertionIndex = insertionIndex(e);
                        dragged.getTabPane().getTabs().remove(dragged);
                        tabPane.getTabs().add(insertionIndex, dragged);
                        tabPane.requestFocus();
                    }
                    case RIGHT -> {
                        dragged.getTabPane().getTabs().remove(dragged);
                        var dndTabPane = parent.addRight((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                    case LEFT -> {
                        dragged.getTabPane().getTabs().remove(dragged);
                        var dndTabPane = parent.addLeft((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                    case TOP -> {
                        dragged.getTabPane().getTabs().remove(dragged);
                        var dndTabPane = parent.addTop((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                    case BOTTOM -> {
                        dragged.getTabPane().getTabs().remove(dragged);
                        var dndTabPane = parent.addBottom((EditorPane) dragged.getContent());
                        ensureFocus(dndTabPane);
                    }
                }
                if (unplug) {
                    from.parent.parent.remove(from.parent);
                }
                initTab(dragged);
                tabPane.getSelectionModel().select(dragged);
            }
            e.consume();
            e.setDropCompleted(true);
        }
        private void handleDragExited(DragEvent e) {
            marker.setVisible(false);
        }
        private void handleDragDone(DragEvent e) {
            marker.setVisible(false);
            draggedTab.set(null);
            e.consume();
        }
        private int insertionIndex(DragEvent e) {
            int insertion = 0;
            for (Tab tab : tabPane.getTabs()) {
                Node tabNode = tabNode(tab.getGraphic());
                Bounds bounds = tabNode.localToScreen(tabNode.getBoundsInLocal());
                if (e.getScreenX() < bounds.getCenterX()) {
                    return insertion;
                }
                insertion++;
            }
            return insertion;
        }
        @Override
        public void setParent(SplitTabPane parent) { this.parent = parent; }
        private TabPane getTabPane() { return tabPane; }
    }

    enum DropPoint { HEADER, TOP, RIGHT, BOTTOM, LEFT, ANY; }

    private static DropPoint dropPoint(Node node, DragEvent e) {
        Bounds paneBounds = node.localToScreen(node.getBoundsInLocal());
        double w = paneBounds.getWidth() / 4;
        double h = paneBounds.getHeight() / 4;
        if (new BoundingBox(
                paneBounds.getMinX(),
                paneBounds.getMinY(),
                paneBounds.getWidth(),
                25 * 2).contains(e.getScreenX(), e.getScreenY())) {
            return DropPoint.HEADER;
        } else if (new BoundingBox(
                paneBounds.getMaxX() - w,
                paneBounds.getMinY() + h,
                w,
                paneBounds.getHeight() - h * 2).contains(e.getScreenX(), e.getScreenY())) {
            return DropPoint.RIGHT;
        } else if (new BoundingBox(
                paneBounds.getMinX() + w,
                paneBounds.getMaxY() - h,
                paneBounds.getWidth() - w * 2,
                h).contains(e.getScreenX(), e.getScreenY())) {
            return DropPoint.BOTTOM;
        } else if (new BoundingBox(
                paneBounds.getMinX(),
                paneBounds.getMinY() + h,
                w,
                paneBounds.getHeight() - h * 2).contains(e.getScreenX(), e.getScreenY())) {
            return DropPoint.LEFT;
        } else if (new BoundingBox(
                paneBounds.getMinX() + w,
                paneBounds.getMinY(),
                paneBounds.getWidth() - w * 2,
                h).contains(e.getScreenX(), e.getScreenY())) {
            return DropPoint.TOP;
        } else {
            return DropPoint.ANY;
        }
    }

    private static Image tabImage(Node node) {
        node = tabNode(node);
        var snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.TRANSPARENT);
        var snapshot = node.snapshot(snapshotParams, null);
        return snapshot;
    }

    private static Node tabNode(Node node) {
        for (;;) {
            node = node.getParent();
            if (Objects.equals(
                    node.getClass().getSimpleName(),
                    "TabHeaderSkin")) return node;
        }
    }

    private static void ensureFocus(DndTabPane dndTabPane) {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(17);
                } catch (InterruptedException ignore) {
                }
                Platform.runLater(dndTabPane::focus);
                return null;
            }
        }).start();
    }

}
