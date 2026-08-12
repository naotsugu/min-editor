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
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The Context.
 * @author Naotsugu Kobayashi
 */
public class Context {

    // last -> front
    private final ObservableList<Stage> stages = FXCollections.observableArrayList();
    private final ObservableMap<Scene, Tab> latestTab = FXCollections.observableHashMap();
    private final AtomicReference<Tab> dragged = new AtomicReference<>();

    private final boolean duplicateContentAllowed;

    private final Function<Object, ? extends ContentPane> toContent;
    private final BiFunction<Stage, Pane, Scene> toScene;

    public Context(
            boolean duplicateContentAllowed,
            Function<Object, ? extends ContentPane> toContent,
            BiFunction<Stage, Pane, Scene> toScene) {
        this.duplicateContentAllowed = duplicateContentAllowed;
        this.toContent = toContent;
        this.toScene = toScene;
    }

    public void addStage(Stage stage) {
        stages.add(stage);
        stage.focusedProperty().addListener((_, _, focused) -> {
            // sort by z-order
            if (focused && stages.remove(stage)) stages.add(stage);
        });
        stage.setOnHidden(_ -> stages.remove(stage));
    }

    void toFrontAll() {
        stages().stream()
            .filter(Predicate.not(Stage::isIconified))
            .filter(Predicate.not(Stage::isAlwaysOnTop))
            .forEach(Stage::toFront);
    }

    List<Stage> stages() {
        return stages.stream().toList();
    }

    void dragStart(Tab tab) {
        dragged.set(tab);
    }

    Tab draggedTab() {
        return dragged.get();
    }

    public void dragDone() {
        dragged.set(null);
    }

    public void handleTabSelected(ObservableValue<? extends javafx.scene.control.Tab> observable,
            javafx.scene.control.Tab oldValue, javafx.scene.control.Tab newValue) {
        if (newValue instanceof Tab selected && selected.parent() != null && selected.parent().getScene() != null) {
            Platform.runLater(() -> selected.content().focus());
            latestTab.put(selected.parent().getScene(), selected);
        }
    }

    public void handleTabRemoved(ListChangeListener.Change<? extends javafx.scene.control.Tab> change) {
        while (change.next()) {
            for (var removed : change.getRemoved()) {
                if (removed instanceof Tab tab && tab.parent() != null && tab.parent().getScene() != null) {
                    latestTab.remove(tab.parent().getScene(), tab);
                }
            }
        }
    }

    public ContentPane createContentPane() {
        return createContentPane(null);
    }

    public ContentPane createContentPane(Object arg) {
        if (!duplicateContentAllowed && arg != null) {
            var dup = allTabs().stream()
                .filter(tab -> tab.content().matches(arg))
                .toList();
            if (!dup.isEmpty()) {
                dup.getFirst().requestSelect();
                return null;
            }
        }
        return toContent.apply(arg);
    }

    public Scene toScene(Stage stage, BranchNode branchNode) {
        addStage(stage);
        return toScene.apply(stage, branchNode);
    }

    List<Tab> allTabs() {
        return stages.stream()
            .map(stage -> stage.getScene().getRoot().lookupAll("." + LeafNode.STYLE_CLASS))
            .flatMap(Collection::stream)
            .map(LeafNode.class::cast)
            .map(LeafNode::children)
            .flatMap(Collection::stream)
            .toList();
    }

}
