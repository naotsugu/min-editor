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
import javafx.scene.Node;
import javafx.stage.Stage;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * The Context.
 * @author Naotsugu Kobayashi
 */
public class Context {

    private static final System.Logger log = System.getLogger(Context.class.getName());

    static final String TAB_SELECTED = "tab-container-selected";

    // last -> front
    private final ObservableList<Stage> stages = FXCollections.observableArrayList();
    // recent -> old
    private final ObservableMap<Stage, SequencedSet<Tab>> lruTabs = FXCollections.observableHashMap();
    private final AtomicReference<Tab> dragged = new AtomicReference<>();
    private final Handlers handlers;

    public Context(Handlers handlers) {
        this.handlers = handlers;
        handlers.addStageHandler(this::addStage);
    }

    public void addStage(Stage stage) {
        stages.add(stage);
        lruTabs.put(stage, new LinkedHashSet<>());
        stage.focusedProperty().addListener((_, _, focused) -> {
            // sort by z-order
            if (focused && stages.remove(stage)) stages.add(stage);
        });
        stage.setOnHidden(_ -> { stages.remove(stage); lruTabs.remove(stage); });
        stage.setOnShown(_ -> findAllTabs());
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

    public void handleTabChanged(ListChangeListener.Change<? extends javafx.scene.control.Tab> change) {
        while (change.next()) {
            for (var added : change.getAddedSubList()) {
                if (added instanceof Tab tab) {
                    if (tab.parent() != null && tab.parent().getScene() != null) {
                        referOnLru(tab);
                    } else {
                        log.log(System.Logger.Level.WARNING, "Tab " + tab.parent().getScene() + " has no scene");
                    }
                }
            }
            for (var removed : change.getRemoved()) {
                if (removed instanceof Tab tab) {
                    removeOnLru(tab);
                }
            }
        }
    }

    void focus(Tab tab) {
        Platform.runLater(() -> tab.content().focus());
        referOnLru(tab);
    }

    Handlers handlers(LeafNode leafNode) {
        focus(leafNode.selectedTab());
        return handlers;
    }

    Handlers handlers() {
        return handlers;
    }

    Optional<ContentPane> find(Predicate<ContentPane> predicate) {
        return allTabs().stream().map(Tab::content).filter(predicate).findFirst();
    }

    Tab currentTab() {
        Stage stage = (Stage) stages.getLast().getScene().getWindow();
        SequencedSet<Tab> lru = lruTabs.get(stage);
        return lru.getFirst();
    }

    List<Tab> allTabs() {
        return stages.stream().map(lruTabs::get).flatMap(Collection::stream).toList();
    }

    void referOnLru(Tab tab) {
        Stage stage = (Stage) tab.parent().getScene().getWindow();
        SequencedSet<Tab> lru = lruTabs.get(stage);
        if (!lru.isEmpty()) {
            lru.getFirst().getStyleClass().remove(TAB_SELECTED);
        }
        tab.getStyleClass().add(TAB_SELECTED);
        lru.remove(tab);
        lru.addFirst(tab);
    }

    void removeOnLru(Tab tab) {
        for (Stage stage : stages) {
            if (lruTabs.get(stage).remove(tab)) {
                if (tab.getStyleClass().contains(TAB_SELECTED)) {
                    lruTabs.get(stages.getLast()).getFirst().getStyleClass().add(TAB_SELECTED);
                }
                break;
            }
        }
    }

    private void findAllTabs() {
        for (Stage stage : stages) {
            SequencedSet<Tab> lru = lruTabs.get(stage);
            Set<Node> nodes = stage.getScene().getRoot().lookupAll("." + LeafNode.STYLE_CLASS);
            for (Node node : nodes) {
                if (node instanceof LeafNode leafNode) {
                    leafNode.children().forEach(tab -> {
                        if (!lru.contains(tab)) {
                            lru.addLast(tab);
                        }
                    });
                }
            }
        }
    }

}
