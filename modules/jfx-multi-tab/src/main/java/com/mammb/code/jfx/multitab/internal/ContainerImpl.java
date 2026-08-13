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

import com.mammb.code.jfx.multitab.Container;
import com.mammb.code.jfx.multitab.ContentPane;
import javafx.geometry.Side;
import javafx.scene.Node;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The ContainerImpl.
 * @author Naotsugu Kobayashi
 */
public class ContainerImpl implements Container {

    private final ContentPane pane;

    public ContainerImpl(ContentPane pane) {
        this.pane = pane;
    }

    @Override
    public ContentPane add() {
        var leafNode = leafNode();
        if (leafNode.isEmpty()) {
            return null;
        }
        var leaf = leafNode.get();
        ContentPane contentPane = leaf.context().createEmptyContent();
        leaf.add(contentPane);
        return contentPane;
    }

    @Override
    public ContentPane add(Path path) {
        if (path == null || !Files.exists(path) ||
            !Files.isRegularFile(path) || !Files.isReadable(path)) return null;
        var leafNode = leafNode();
        if (leafNode.isEmpty()) {
            return null;
        }
        var leaf = leafNode.get();
        ContentPane contentPane = leaf.context().createContentPane(path, pane.container());
        leaf.add(contentPane);
        return contentPane;
    }

    @Override
    public void add(Side side, ContentPane contentPane) {
        leafNode().ifPresent(l -> l.add(contentPane, side));
    }

    @Override
    public Optional<ContentPane> find(Predicate<ContentPane> predicate) {
        return leafNode().flatMap(leaf -> leaf.context().find(predicate));
    }

    @Override
    public void select(ContentPane contentPane) {
        leafNode().ifPresent(leafNode ->
            leafNode.context().allTabs().stream()
                .filter(tab -> Objects.equals(tab.content(), contentPane))
                .forEach(Tab::requestSelect));
    }

    private Optional<LeafNode> leafNode() {
        for (Node node = pane.getParent(); node != null; node = node.getParent()) {
            if (node instanceof LeafNode leafNode) {
                return Optional.of(leafNode);
            }
        }
        return Optional.empty();
    }
}
