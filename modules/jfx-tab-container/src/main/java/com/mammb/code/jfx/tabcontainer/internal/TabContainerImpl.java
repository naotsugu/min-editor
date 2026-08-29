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
import com.mammb.code.jfx.tabcontainer.TabContainer;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The TabContainerImpl.
 * @author Naotsugu Kobayashi
 */
public class TabContainerImpl implements TabContainer, ContainerHandle {

    private final Context ctx;

    public TabContainerImpl(
            RequireContent requireContent,
            RequestContent requestContent,
            RequireStage requireStage,
            TabMenuItemDecorator tabMenuDecorator,
            TabHeaderMenuItemDecorator tabHeaderMenuDecorator) {

        this.ctx = new Context(new Handlers(
            this,
            requireContent,
            requestContent,
            requireStage,
            tabMenuDecorator,
            tabHeaderMenuDecorator));
    }

    @Override
    public Pane resume(Stage stage, Path path, ResumeContent resumeContent) {
        var suspend = new Suspend(path);
        var suspendHandler = new SuspendHandler(suspend);
        suspendHandler.bind(stage);
        ctx.handlers().addStageHandler(suspendHandler::bind);
        ctx.addStage(stage);
        var pane = new Resume(ctx, path).load(stage, resumeContent::apply);
        Platform.runLater(() -> select(null));
        return pane;
    }

    @Override
    public Pane create(Stage stage) {
        stage.setWidth(600);
        stage.setHeight(400);
        ctx.addStage(stage);
        var pane = new BranchNode(ctx, ctx.handlers().requireContent());
        Platform.runLater(() -> select(null));
        return pane;
    }

    @Override
    public void add(ContentPane contentPane) {
        ctx.currentTab().parent().add(contentPane);
    }

    @Override
    public void add(Side side, ContentPane contentPane) {
        ctx.currentTab().parent().add(contentPane, side);
    }

    @Override
    public Optional<ContentPane> findFirst(Predicate<ContentPane> predicate) {
        return ctx.findFirst(predicate);
    }

    @Override
    public List<ContentPane> find(Predicate<ContentPane> predicate) {
        return ctx.find(predicate);
    }

    @Override
    public void select(ContentPane contentPane) {
        List<Tab> tabs = ctx.allTabs();
        tabs.stream()
            .filter(tab -> Objects.equals(tab.content(), contentPane))
            .findFirst().ifPresentOrElse(
                Tab::requestSelect,
                () -> tabs.getFirst().requestSelect()
            );
    }

}
