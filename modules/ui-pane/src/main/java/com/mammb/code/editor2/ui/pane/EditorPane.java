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

import javafx.scene.AccessibleRole;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import java.nio.file.Path;

/**
 * EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    private Canvas canvas;
    private GraphicsContext gc;
    private EditorModel editorModel;
    double margin = 5;

    public EditorPane(double width, double height) {
        setWidth(width);
        setHeight(height);
        double canvasWidth = width - margin;
        double canvasHeight = height - margin;
        editorModel = new EditorModel(canvasWidth, canvasHeight);
        canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.setLayoutX(margin);
        canvas.setLayoutY(margin);
        gc = canvas.getGraphicsContext2D();
        setFocusTraversable(true);
        setAccessibleRole(AccessibleRole.TEXT_AREA);
        initHandler();
        getChildren().add(canvas);
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        setOnKeyPressed(this::handle);
        setOnKeyTyped(this::handle);
        setOnScroll(this::handle);
        setOnMouseClicked(this::handle);
        setOnDragOver(DragDrops.dragOverHandler());
        setOnDragDropped(DragDrops.droppedHandler(this::open));
    }


    /**
     * Open the file content path.
     * @param path the content file path
     */
    public void open(Path path) {
        editorModel = new EditorModel(getWidth(), getHeight(), path);
        editorModel.draw(gc);
        requestFocus();
    }


    public void handle(ScrollEvent e) {

        if (e.getEventType() == ScrollEvent.SCROLL) {
            if (e.getDeltaY() > 0) {
                editorModel.up(Math.min((int) e.getDeltaY(), 3));
            } else if (e.getDeltaY() < 0) {
                editorModel.down(Math.min(Math.abs((int) e.getDeltaY()), 3));
            }
            editorModel.clearAndDraw(gc);
        }
    }


    public void handle(KeyEvent e) {
        switch (e.getCode()) {
            case RIGHT      -> editorModel.moveCaretRight();
            case LEFT       -> editorModel.moveCaretLeft();
            case UP         -> editorModel.moveCaretUp();
            case DOWN       -> editorModel.moveCaretDown();
        }
        editorModel.clearAndDraw(gc);
    }

//    public void handle(KeyEvent e) {
//
//        if (e.getCode().isFunctionKey() || e.getCode().isNavigationKey() ||
//            e.getCode().isArrowKey() || e.getCode().isModifierKey() ||
//            e.getCode().isMediaKey() || !Keys.controlKeysFilter.test(e) ||
//            e.getCharacter().length() == 0) {
//            return;
//        }
//        int ascii = e.getCharacter().getBytes()[0];
//        if (ascii < 32 || ascii == 127) {
//            // 127:DEL
//            if (ascii != 9 && ascii != 10 && ascii != 13) {
//                // 9:HT 10:LF 13:CR
//                return;
//            }
//        }
//        //editBehavior.input(e.getCharacter());
//
//    }


    public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            //caretBehavior.click(event.getSceneX(), event.getSceneY());
        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            //caretBehavior.clickDouble(event.getSceneX(), event.getSceneY());
        }
    }

}
