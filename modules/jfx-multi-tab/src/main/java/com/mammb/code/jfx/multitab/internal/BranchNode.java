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
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class BranchNode extends TreeNode implements ParentOf<TreeNode> {

    private final SplitPane splitPane = new SplitPane();
    private final Context ctx;
    private BranchNode parent;
    private double prefDividerPositions = 0.5;

    BranchNode(Context ctx, BranchNode parent) {
        this.ctx = Objects.requireNonNull(ctx);
        this.parent = parent;
        getChildren().add(splitPane);
    }

    public BranchNode(Context ctx, ContentPane content) {
        this(ctx, (BranchNode) null);
        addChildren(List.of(new LeafNode(ctx, content)));
    }

    public BranchNode(Context ctx) {
        this(ctx, (BranchNode) null);
    }

    public void add(ContentPane content, LeafNode source, Side side) {

        int sourceIndex = children().indexOf(source);
        if (sourceIndex < 0) throw new IllegalArgumentException("No such node in content");

        Orientation orientation = switch (side) {
            case TOP, BOTTOM -> Orientation.VERTICAL;
            default -> Orientation.HORIZONTAL;
        };

        if (children().size() <= 1) {
            // add new leaf pane
            int insIndex = (side == Side.RIGHT || side == Side.BOTTOM)
                ? sourceIndex + 1
                : sourceIndex;
            addChild(insIndex, new LeafNode(ctx, content));
            splitPane.setOrientation(orientation);
        } else {
            removeChild(source);
            BranchNode newChild = new BranchNode(ctx, this);
            newChild.orientation(orientation);
            newChild.addChildren(List.of(source));
            int insIndex = (side == Side.RIGHT || side == Side.BOTTOM) ? 1 : 0;
            newChild.addChild(insIndex, new LeafNode(ctx, content));
            addChild(sourceIndex, newChild);
        }
    }

    void eject(TreeNode node) {
        removeChild(node);
        balance();
    }

    private void balance() {

        List<TreeNode> children = children();
        if (isRoot() && children.isEmpty()) {
            if (ctx.stages().size() == 1) {
                addChildren(List.of(new LeafNode(ctx, ctx.contentSupplier().apply(""))));
            } else {
                ((Stage) getScene().getWindow()).close();
            }
        }

        if (isRoot()) return;

        int childrenSize = children.size();
        if (childrenSize <= 1) {
            if (childrenSize == 1) {
                parent.addChild(parent.children().indexOf(this), children.getFirst());
            }
            BranchNode prevParent = parent;
            parent.removeChild(this);
            prevParent.balance();
            return;
        }
        parent.balance();
    }

    @Override
    public BranchNode parent() {
        return parent;
    }

    @Override
    public void parent(BranchNode parent) {
        this.parent = parent;
    }

    @Override
    public List<TreeNode> children() {
        return splitPane.getItems().stream()
            .filter(TreeNode.class::isInstance)
            .map(TreeNode.class::cast)
            .toList();
    }

    @Override
    public void addChildren(List<TreeNode> children) {
        for (TreeNode child : children) {
            child.parent(this);
            splitPane.getItems().add(child);
        }
    }

    @Override
    public void addChild(int index, TreeNode child) {
        child.parent(this);
        splitPane.getItems().add(index, child);
    }

    @Override
    public boolean removeChild(TreeNode child) {
        child.parent(null);
        return splitPane.getItems().remove(child);
    }

    public Orientation orientation() {
        return splitPane.getOrientation();
    }

    public void orientation(Orientation value) {
        splitPane.setOrientation(value);
    }

    public double[] dividerPositions() {
        return splitPane.getDividerPositions();
    }

    public void dividerPositions(double[] value) {
        splitPane.setDividerPositions(value);
    }

    void maximize(TreeNode node) {
        if (root().leaves().size() == 1) return;
        List<TreeNode> children = children();
        if (children.size() > 1) {
            double[] div = splitPane.getDividerPositions();
            prefDividerPositions = (div != null && div.length > 0) ? splitPane.getDividerPositions()[0] : 0.5;
            splitPane.setDividerPositions((children.indexOf(node) == 1) ? 0 : 1);
        }
        if (parent != null) {
            parent.maximize(this);
        }
    }

    void unmaximize() {
        if (prefDividerPositions <= 0 || prefDividerPositions >= 1) {
            prefDividerPositions = 0.5;
        }
        splitPane.setDividerPositions(prefDividerPositions);
        if (parent != null) {
            parent.unmaximize();
        }
    }

    boolean isMaximized(TreeNode node) {
        return root().leaves().stream().filter(c -> node != c).allMatch(LeafNode::isFolded);
    }


    List<LeafNode> leaves() {
        return children().stream()
            .map(c -> switch (c) {
                case LeafNode leaf -> List.of(leaf);
                case BranchNode branch -> branch.leaves();
                case null, default -> List.of();
            })
            .flatMap(Collection::stream)
            .filter(LeafNode.class::isInstance)
            .map(LeafNode.class::cast)
            .toList();
    }

}
