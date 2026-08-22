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
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class SuspendHandler {
    private final Suspend suspend;

    SuspendHandler(Suspend suspend) {
        this.suspend = suspend;
    }

    Stage bind(Stage stage) {
        stage.setOnCloseRequest(this::handleStageCloseRequest);
        stage.setOnHiding(this::handleStageHiding);
        return stage;
    }

    private void handleStageCloseRequest(WindowEvent event) {
        if (event.getTarget() instanceof Stage stage) {
            Scene scene = stage.getScene();
            if (scene == null) {
                return;
            }

            if (scene.getRoot().lookup("." + BranchNode.STYLE_CLASS) instanceof BranchNode branchNode) {

                Predicate<ContentPane> predicate = (Stage.getWindows().stream().filter(Window::isShowing).count() > 1)
                    ? ContentPane::canCloseQuiet
                    : ContentPane::canExitQuiet;

                List<ContentPane> contentPanes = branchNode.leaves().stream()
                    .map(LeafNode::children)
                    .flatMap(Collection::stream)
                    .map(Tab::content)
                    .filter(Predicate.not(predicate))
                    .toList();

                for (ContentPane contentPane : contentPanes) {
                    if (!contentPane.closeRequest()) {
                        event.consume();
                        return;
                    }
                }
            }
        }
    }

    private void handleStageHiding(WindowEvent event) {
        if (Stage.getWindows().stream().noneMatch(Window::isShowing)) {
            if (event.getTarget() instanceof Stage stage) {
                suspend.save(stage);
            }
        }
    }

}
