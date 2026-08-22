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
package com.mammb.code.jfx.tabcontainer.internal;

import com.mammb.code.jfx.tabcontainer.ContentPane;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * The Context.
 * @author Naotsugu Kobayashi
 */
public class Context {

    static final String TAB_SELECTED = "tab-container-selected";

    // last -> front
    private final ObservableList<Stage> stages = FXCollections.observableArrayList();
    private final ObservableMap<Scene, Tab> latestTab = FXCollections.observableHashMap();
    private final AtomicReference<Tab> dragged = new AtomicReference<>();
    private final Handlers handlers;

    public Context(Handlers handlers) {
        this.handlers = handlers;
        handlers.addStageHandler(this::addStage);
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
            focus(selected);
        }
    }

    public void handleTabRemoved(ListChangeListener.Change<? extends javafx.scene.control.Tab> change) {
        while (change.next()) {
            for (var removed : change.getRemoved()) {
                if (removed instanceof Tab tab && tab.parent() != null && tab.parent().getScene() != null) {
                    unfocus(tab);
                }
            }
        }
    }

    void focus(Tab tab) {
        Platform.runLater(() -> tab.content().focus());
        var key = tab.parent().getScene();
        Optional.ofNullable(latestTab.get(key)).ifPresent(this::unfocus);
        tab.getStyleClass().add(TAB_SELECTED);
        latestTab.put(tab.parent().getScene(), tab);
    }

    void unfocus(Tab tab) {
        tab.getStyleClass().remove(TAB_SELECTED);
    }


    Handlers handlers() {
        return handlers;
    }

    Optional<ContentPane> find(Predicate<ContentPane> predicate) {
        return allTabs().stream().map(Tab::content).filter(predicate).findFirst();
    }

    Tab currentTab() {
        Scene scene = stages.getLast().getScene();
        var tab = latestTab.get(scene);
        if (tab != null) {
            return tab;
        } else if (!latestTab.isEmpty()) {
            return latestTab.values().stream().findFirst().get();
        } else {
            return allTabs().getLast();
        }
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
