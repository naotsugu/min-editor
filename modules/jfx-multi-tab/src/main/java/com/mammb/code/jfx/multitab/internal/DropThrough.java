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
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.util.List;
import java.util.Objects;

class DropThrough extends Pane {

    private final Stage stage;
    private final Context ctx;

    private DropThrough(Context ctx, Rectangle2D bounds) {

        this.ctx = Objects.requireNonNull(ctx);

        // on Windows, transparent areas do not accept mouse events, so set it to semi-transparent
        setStyle((File.separatorChar == '\\')
            ? "-fx-background-color: rgba(0, 0, 0, 0.01);"
            : "-fx-background-color: transparent;");

        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);

        var scene = new Scene(this);
        scene.setFill(Color.TRANSPARENT);

        ctx.stages().stream()
            .map(s -> new Rectangle(
                s.getX() - bounds.getMinX(),
                s.getY() - bounds.getMinY(),
                s.getWidth(), s.getHeight()))
            .peek(shape -> shape.setFill(Color.TRANSPARENT))
            .peek(shape -> shape.setOnDragEntered(_ -> ctx.toFrontAll()))
            .forEach(shape -> getChildren().add(shape));

        stage = new Stage();
        // stage.initOwner(owner); do not use
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle(ctx.stages().stream().map(Stage::getTitle).filter(Objects::nonNull).findFirst().orElse(""));
        stage.setScene(scene);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.show();
    }

    public static List<DropThrough> create(Context ctx) {
        List<DropThrough> list = Screen.getScreens().stream()
            .map(screen -> new DropThrough(ctx, screen.getVisualBounds()))
            .toList();
        ctx.toFrontAll();
        return list;
    }

    private void handleDragOver(DragEvent e) {
        Dragboard db = e.getDragboard();
        Tab tab = ctx.draggedTab();
        if (!db.hasContent(Tab.TAB_MOVE_FORMAT) || tab == null) return;
        e.acceptTransferModes(TransferMode.MOVE);
        e.consume();
    }

    private void handleDragDropped(DragEvent e) {

        Dragboard db = e.getDragboard();
        Tab tab = ctx.draggedTab();
        if (!db.hasContent(Tab.TAB_MOVE_FORMAT) || tab == null) return;

        Stage nextStage = new Stage();
        ContentPane content = tab.content();
        Scene scene = new Scene(new BranchNode(ctx, content));
        ctx.addStage(nextStage);
        stage.setScene(scene);
        stage.setWidth(content.getWidth());
        stage.setHeight(content.getHeight());
        stage.setX(e.getScreenX() - content.getWidth() / 2);
        stage.setX(e.getScreenY() - content.getHeight() / 2);
        nextStage.show();

        e.setDropCompleted(true);
        e.consume();
    }

    public void close() {
        stage.close();
    }

}
