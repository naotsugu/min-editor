/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor2.ui.pane;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.AccessibleRole;
import java.nio.file.Path;

/**
 * EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    private Canvas canvas;
    private GraphicsContext gc;
    private EditorModel editorModel;

    public EditorPane(double width, double height) {
        setWidth(width);
        setHeight(height);
        editorModel = new EditorModel(width, height);
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        setFocusTraversable(true);
        setAccessibleRole(AccessibleRole.TEXT_AREA);
        getChildren().add(canvas);
        initHandler();
    }

    /**
     * Initialize handler.
     */
    private void initHandler() {
        setOnDragOver(DragDrop.dragOverHandler());
        setOnDragDropped(DragDrop.droppedHandler(this::open));
    }

    /**
     * Open the file content path.
     * @param path the content file path
     */
    public void open(Path path) {
        editorModel = new EditorModel(getWidth(), getHeight(), path);
        editorModel.draw(gc);
    }

}
