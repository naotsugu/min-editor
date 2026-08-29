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
package com.mammb.code.jfx.tabcontainer;

import com.mammb.code.jfx.tabcontainer.internal.TabContainerImpl;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * The TabContainer.
 * @author Naotsugu Kobayashi
 */
public interface TabContainer extends ContainerHandle {

    interface RequireContent { ContentPane content(); }
    interface RequireStage { Stage stage(Pane pane); }
    interface RequestContent { void accept(ContainerHandle containerHandle, Path path); }
    interface TabMenuItemDecorator { MenuItem[] apply(ContentPane contentPane, MenuItem... items); }
    interface TabHeaderMenuItemDecorator { MenuItem[] apply(ContainerHandle containerHandle, MenuItem... items); }
    interface ResumeContent { ContentPane apply(String string); }


    static TabContainer of(
            RequireContent requireContent,
            RequestContent requestContent,
            RequireStage requireStage) {
        return new TabContainerImpl(
            requireContent,
            requestContent,
            requireStage,
            (_, items) -> items,
            (_, items) -> items);
    }

    static TabContainer of(
        RequireContent requireContent,
        RequestContent requestContent,
        RequireStage requireStage,
        TabMenuItemDecorator tabMenuDecorator) {
        return new TabContainerImpl(
            requireContent,
            requestContent,
            requireStage,
            tabMenuDecorator,
            (_, items) -> items);
    }

    static TabContainer of(
        RequireContent requireContent,
        RequestContent requestContent,
        RequireStage requireStage,
        TabMenuItemDecorator tabMenuDecorator,
        TabHeaderMenuItemDecorator tabHeaderMenuDecorator) {
        return new TabContainerImpl(
            requireContent,
            requestContent,
            requireStage,
            tabMenuDecorator,
            tabHeaderMenuDecorator);
    }

    Pane resume(Stage stage, Path path, ResumeContent resumeContent);

    Pane create(Stage stage);

}
