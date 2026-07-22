/*
 * Copyright 2026- the original author or authors.
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
package com.mammb.code.jfx.multitab.internal;

import com.mammb.code.jfx.multitab.ContentPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LeafNode extends TreeNode implements ParentOf<Tab> {

    private static final System.Logger log = System.getLogger(LeafNode.class.getName());

    private final TabPane tabPane = new TabPane();
    private final DropMarker dropMarker = new DropMarker();
    private final Context ctx;
    private BranchNode parent;

    public LeafNode(Context ctx, ContentPane content) {
        this.ctx = Objects.requireNonNull(ctx);
        initTabPane();
        getChildren().addAll(tabPane, dropMarker);
        addChildren(Objects.isNull(content)
            ? List.of()
            : List.of(new Tab(ctx, content)));

        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragExited(this::handleDragExited);
    }

    public LeafNode(Context ctx) {
        this(ctx, null);
    }

    private void initTabPane() {
        tabPane.setRotateGraphic(true);
        tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.getSelectionModel().selectedItemProperty().addListener(ctx::handleTabSelected);
        tabPane.getTabs().removeListener(ctx::handleTabRemoved);
        tabPane.layoutBoundsProperty().addListener(this::handleTabPaneLayoutBoundsChanged);
        TabButton.install(tabPane, () -> new Tab(ctx, this, ctx.contentSupplier().apply("")));
        initTabHeaderArea();
    }

    private void initTabHeaderArea() {

        Node headerArea = tabHeaderArea();

        if (headerArea == null) {
            // ensure the initialization of headerArea.
            tabPane.skinProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Skin<?>> ob, Skin<?> old, Skin<?> skin) {
                    if (skin != null) {
                        tabPane.applyCss();
                        initTabHeaderArea();
                        tabPane.skinProperty().removeListener(this);
                    }
                }
            });
            return;
        }

        // fix the minimum width/height of this panel.
        headerArea.boundsInLocalProperty().addListener((_, _, boundsInLocal) -> {
            setMinWidth(boundsInLocal.getHeight());
            setMinHeight(boundsInLocal.getHeight());
        });

        headerArea.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                if (parent().isMaximized(this)) {
                    parent().unmaximize();
                } else {
                    parent().maximize(this);
                }
            }
        });

        headerArea.setOnContextMenuRequested(event ->
            buildTabHeaderContextMenu()
                .show(tabPane, event.getScreenX(), event.getScreenY()));

    }

    private void handleDragOver(DragEvent e) {

        dropMarker.clear();
        boolean dragOnTabHeader = dragOnTabHeader(e);

        if (e.getDragboard().hasFiles() && dragOnTabHeader) {
            Node tabHeaderArea = tabHeaderArea();
            dropMarker.show(innerBounds(
                tabHeaderArea.localToParent(tabHeaderArea.getBoundsInLocal()),
                dropMarker.getStrokeWidth()));
            e.acceptTransferModes(TransferMode.COPY);
            e.consume();
            return;
        }

        Dragboard db = e.getDragboard();
        Tab dragged = ctx.draggedTab();
        if (!db.hasContent(Tab.TAB_MOVE_FORMAT) || dragged == null) return;

        if (dragOnTabHeader) {
            int insertionIndex = insertionIndex(e);
            List<Tab> tabs = children();
            int tabIndex = Math.min(tabs.size() - 1, insertionIndex);
            Node tabNode = tabs.get(tabIndex).tabNode();
            Bounds tabBounds = tabNode.getBoundsInLocal();
            Bounds bounds = new BoundingBox(
                (insertionIndex > tabIndex) ? tabBounds.getMaxX() : tabBounds.getMinX(),
                tabBounds.getMinY(),
                dropMarker.getStrokeWidth(),
                tabBounds.getHeight()
            );
            dropMarker.show(dropMarker.screenToLocal(tabNode.localToScreen(bounds)));
            e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        } else {
            dragOnSide(e).ifPresent(side -> {
                dropMarker.show(innerBounds(getLayoutBounds(), dropMarker.getStrokeWidth()), side);
                e.acceptTransferModes(TransferMode.MOVE);
                e.consume();
            });
        }
    }

    private void handleDragDropped(DragEvent e) {

        dropMarker.clear();
        Dragboard db = e.getDragboard();
        boolean dragOnTabHeader = dragOnTabHeader(e);

        if (e.getDragboard().hasFiles() && dragOnTabHeader) {
            List<Path> paths = db.getFiles().stream()
                .filter(File::exists).filter(File::canRead).map(File::toPath).toList();
            paths.stream()
                .map(ctx.pathContentSupplier())
                .map(contentPane -> new Tab(ctx, contentPane))
                .forEach(tab -> addChildren(List.of(tab)));
            if (paths.isEmpty()) {
                e.setDropCompleted(true);
                e.consume();
            }
            return;
        }

        Tab tab = ctx.draggedTab();
        if (!db.hasContent(Tab.TAB_MOVE_FORMAT) || tab == null) return;

        if (dragOnTabHeader) {
            int tabIndex = Math.min(tabPane.getTabs().size(), insertionIndex(e));
            addChild(tabIndex, new Tab(ctx, tab.content()));
            e.setDropCompleted(true);
            e.consume();
            return;
        }

        dragOnSide(e).ifPresent(side -> {
            parent.add(tab.content(), this, side);
            e.setDropCompleted(true);
            e.consume();
        });
        e.consume();
    }

    private void handleDragExited(DragEvent e) {
        dropMarker.clear();
    }

    void close(Tab tab) {
        removeChild(tab);
        if (children().isEmpty()) {
            parent.eject(this);
        }
    }

    void closeAll() {
        children().forEach(Tab::requestClose);
    }

    void closeOther(Tab node) {
        children().stream().filter(c -> !Objects.equals(c, node)).forEach(Tab::requestClose);
    }

    void closeRight(Tab node) {
        List<Tab> children = children();
        int index = children.indexOf(node);
        if (index >= 0) {
            children.subList(index + 1, children.size()).forEach(Tab::requestClose);
        }
    }

    void closeLeft(Tab node) {
        List<Tab> children = children();
        int index = children.indexOf(node);
        if (index >= 0) {
            children.subList(0, index).forEach(Tab::requestClose);
        }
    }

    boolean isFolded() {
        return getMinWidth() == getWidth() || getMinHeight() == getHeight();
    }

    @Override
    public BranchNode parent() {
        return parent;
    }

    @Override
    public void parent(BranchNode parent) {
        this.parent = parent;
    }

    private boolean dragOnTabHeader(DragEvent e) {
        Node tabHeaderArea = tabHeaderArea();
        Bounds bounds = tabHeaderArea.localToScreen(tabHeaderArea.getBoundsInLocal());
        return (switch (tabPane.getSide()) {
            case TOP -> new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight() + 20);
            case BOTTOM ->
                new BoundingBox(bounds.getMinX(), bounds.getMinY() - 20, bounds.getWidth(), bounds.getHeight() + 20);
            case LEFT ->
                new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth() + 20, bounds.getHeight());
            case RIGHT ->
                new BoundingBox(bounds.getMinX() - 20, bounds.getMinY(), bounds.getWidth() + 20, bounds.getHeight());
        }).contains(e.getScreenX(), e.getScreenY());
    }

    private Optional<Side> dragOnSide(DragEvent e) {
        Bounds bounds = localToScreen(getBoundsInLocal());
        double w = bounds.getWidth() / 3;
        double h = bounds.getHeight() / 3;
        if (new BoundingBox(
            bounds.getMaxX() - w,
            bounds.getMinY() + h,
            w,
            bounds.getHeight() - h * 2).contains(e.getScreenX(), e.getScreenY())) {
            return Optional.of(Side.RIGHT);
        } else if (new BoundingBox(
            bounds.getMinX() + w,
            bounds.getMaxY() - h,
            bounds.getWidth() - w * 2,
            h).contains(e.getScreenX(), e.getScreenY())) {
            return Optional.of(Side.BOTTOM);
        } else if (new BoundingBox(
            bounds.getMinX(),
            bounds.getMinY() + h,
            w,
            bounds.getHeight() - h * 2).contains(e.getScreenX(), e.getScreenY())) {
            return Optional.of(Side.LEFT);
        } else if (new BoundingBox(
            bounds.getMinX() + w,
            bounds.getMinY(),
            bounds.getWidth() - w * 2,
            h).contains(e.getScreenX(), e.getScreenY())) {
            return Optional.of(Side.TOP);
        }
        return Optional.empty();
    }

    private int insertionIndex(DragEvent e) {
        int insertion = 0;
        for (var tab : children()) {
            Node tabNode = tab.tabNode();
            Bounds bounds = tabNode.getBoundsInLocal();
            Point2D p = tabNode.screenToLocal(e.getScreenX(), e.getScreenY());
            if (p.getX() < bounds.getCenterX()) {
                return insertion;
            }
            insertion++;
        }
        return insertion;
    }

    private Node tabHeaderArea() {
        return tabPane.lookup(".tab-header-area");
    }

    private Bounds innerBounds(Bounds bounds, double gap) {
        return new BoundingBox(
            bounds.getMinX() + gap,
            bounds.getMinY() + gap,
            bounds.getWidth() - (gap * 2),
            bounds.getHeight() - (gap * 2)
        );
    }

    @Override
    public List<Tab> children() {
        return tabPane.getTabs().stream()
            .filter(Tab.class::isInstance)
            .map(Tab.class::cast)
            .toList();
    }

    @Override
    public void addChildren(List<Tab> tabs) {
        for (var tab : tabs) {
            addChild(children().size(), tab);
        }
    }

    @Override
    public void addChild(int index, Tab child) {
        child.parent(this);
        tabPane.getTabs().add(index, child);
        tabPane.getSelectionModel().select(child);
    }

    @Override
    public boolean removeChild(Tab child) {
        child.parent(null);
        return tabPane.getTabs().remove(child);
    }

    /**
     * If the Pane layout is changed, adjust the position of the tab Pane.
     */
    private void handleTabPaneLayoutBoundsChanged(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newBounds) {
        double len = tabPane.lookupAll(".tab").stream()
            .mapToDouble(node -> node.getBoundsInLocal().getWidth())
            .sum();
        Side side = (Math.min(newBounds.getWidth(), newBounds.getHeight()) < len &&
            newBounds.getWidth() < newBounds.getHeight() * 0.5)
            ? Side.LEFT : Side.TOP;
        if (side != tabPane.getSide()) tabPane.setSide(side);
    }

    private ContextMenu buildTabHeaderContextMenu() {
        MenuItem newTab = new MenuItem("New");
        newTab.setOnAction(_ -> addChildren(List.of(new Tab(ctx, ctx.contentSupplier().apply("")))));
        MenuItem closeAll = new MenuItem("Close All");
        closeAll.setOnAction(_ -> closeAll());
        MenuItem maximize = new MenuItem("Maximize");
        maximize.setOnAction(_ -> parent().maximize(this));
        maximize.setDisable(parent.root().leaves().size() <= 1);
        MenuItem restore = new MenuItem("Restore");
        restore.setOnAction(_ -> parent().unmaximize());
        restore.setDisable(!parent.isMaximized(this));
        return new ContextMenu(newTab, closeAll, maximize, restore);
    }

}
