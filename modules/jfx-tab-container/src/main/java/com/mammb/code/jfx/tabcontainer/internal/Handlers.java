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

import com.mammb.code.jfx.tabcontainer.ContainerHandle;
import com.mammb.code.jfx.tabcontainer.ContentPane;
import com.mammb.code.jfx.tabcontainer.TabContainer.*;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Handlers.
 * @author Naotsugu Kobayashi
 */
public class Handlers {

    interface StageHandler { void apply(Stage stage); }
    private final RequireContent requireContent;
    private final RequestContent requestContent;
    private final RequireStage requireStage;
    private final MenuItemDecorator tabMenuDecorator;
    private final MenuItemDecorator tabHeaderMenuDecorator;
    private final List<StageHandler> stageHandlers = new ArrayList<>();

    public Handlers(
        RequireContent requireContent,
        RequestContent requestContent,
        RequireStage requireStage,
        MenuItemDecorator tabMenuDecorator,
        MenuItemDecorator tabHeaderMenuDecorator) {
        this.requireContent = Objects.requireNonNull(requireContent);
        this.requestContent = Objects.requireNonNull(requestContent);
        this.requireStage = Objects.requireNonNull(requireStage);
        this.tabMenuDecorator = Objects.requireNonNull(tabMenuDecorator);
        this.tabHeaderMenuDecorator = Objects.requireNonNull(tabHeaderMenuDecorator);
    }

    public ContentPane requireContent() {
        return requireContent.content();
    }

    public void requestContent(Path path, ContainerHandle container) {
        requestContent.accept(path, container);
    }

    public Stage requireStage(Pane pane) {
        Stage stage = requireStage.stage(pane);
        stageHandlers.forEach(h -> h.apply(stage));
        return stage;
    }

    public MenuItem[] decorateTabMenu(MenuItem... items) {
        return tabMenuDecorator.apply(items);
    }

    public MenuItem[] decorateTabHeaderMenu(MenuItem... items) {
        return tabHeaderMenuDecorator.apply(items);
    }

    void addStageHandler(StageHandler stageHandler) {
        stageHandlers.add(Objects.requireNonNull(stageHandler));
    }

}
