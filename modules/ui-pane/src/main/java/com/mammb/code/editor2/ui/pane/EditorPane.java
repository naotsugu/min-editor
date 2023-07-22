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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.AccessibleRole;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Path;

/**
 * EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    /** The Canvas. */
    private Canvas canvas;
    private GraphicsContext gc;
    private EditorModel editorModel;
    double margin = 5.5;

    /** The timeline. */
    private final Timeline timeline = new Timeline();


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

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), e -> tick()));
        timeline.setCycleCount(-1);
        timeline.play();

    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        setOnKeyPressed(this::handleKeyPressed);
        setOnKeyTyped(this::handleKeyTyped);
        setOnScroll(this::handleScroll);
        setOnMouseClicked(this::handleMouseClicked);
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
    private void openChoose() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select file...");
        fc.setInitialDirectory(initialDirectory(null));
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file != null && file.canRead()) open(file.toPath());
    }
    private void saveChoose() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save As...");
        fc.setInitialDirectory(initialDirectory(null));
        File file = fc.showSaveDialog(getScene().getWindow());
        if (file != null) editorModel.saveAs(file.toPath());
    }

    public void handleScroll(ScrollEvent e) {

        if (e.getEventType() == ScrollEvent.SCROLL) {
            if (e.getDeltaY() > 0) {
                editorModel.scrollPrev(Math.min((int) e.getDeltaY(), 3));
            } else if (e.getDeltaY() < 0) {
                editorModel.scrollNext(Math.min(Math.abs((int) e.getDeltaY()), 3));
            }
            editorModel.draw(gc);
        }
    }


    public void handleMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            switch (event.getClickCount()) {
                case 1 -> editorModel.click(event.getSceneX(), event.getSceneY());
                case 2 -> editorModel.clickDouble(event.getSceneX(), event.getSceneY());
            }
            editorModel.draw(gc);
        }
    }


    public void handleKeyPressed(KeyEvent e) {

        switch (Keys.asAction(e)) {
            case OPEN    -> { openChoose(); return; }
            case SAVE    -> { editorModel.save(); return; }
            case SAVE_AS -> { saveChoose(); return; }
            case WRAP    -> { editorModel.toggleWrap(); editorModel.draw(gc); return; }
            case COPY    -> { editorModel.copyToClipboard(); return; }
            case PASTE   -> { editorModel.pasteFromClipboard();  editorModel.draw(gc); return; }
            case CUT     -> { editorModel.cutToClipboard(); editorModel.draw(gc); return; }
            case UNDO    -> { editorModel.undo(); editorModel.draw(gc); return; }
            case REDO    -> { editorModel.redo(); editorModel.draw(gc); return; }
        }


        if (Keys.isSelectCombination(e)) {
            if (e.isShiftDown()) {
                editorModel.selectOn();
            } else {
                editorModel.selectOff();
            }
        }

        switch (e.getCode()) {
            case RIGHT      -> editorModel.moveCaretRight();
            case LEFT       -> editorModel.moveCaretLeft();
            case UP         -> editorModel.moveCaretUp();
            case DOWN       -> editorModel.moveCaretDown();
            case HOME       -> editorModel.moveCaretLineHome();
            case END        -> editorModel.moveCaretLineEnd();
            case PAGE_UP    -> editorModel.moveCaretPageUp();
            case PAGE_DOWN  -> editorModel.moveCaretPageDown();
            case DELETE     -> editorModel.delete();
            case BACK_SPACE -> editorModel.backspace();
            case ESCAPE     -> editorModel.selectOff();
        }

        editorModel.selectTo();

        editorModel.draw(gc);
    }

    public void handleKeyTyped(KeyEvent e) {
        if (e.getCode().isFunctionKey() || e.getCode().isNavigationKey() ||
            e.getCode().isArrowKey() || e.getCode().isModifierKey() ||
            e.getCode().isMediaKey() || !Keys.controlKeysFilter.test(e) ||
            e.getCharacter().length() == 0) {
            return;
        }

        int ascii = e.getCharacter().getBytes()[0];
        if (ascii < 32 || ascii == 127) {
            // 127:DEL
            if (ascii != 9 && ascii != 10 && ascii != 13) {
                // 9:HT 10:LF 13:CR
                return;
            }
        }
        editorModel.input(e.getCharacter());
        editorModel.draw(gc);
    }

    private void tick() {
        editorModel.tick(gc);
        requestFocus();
    }


    private static File initialDirectory(Path base) {
        return (base == null)
            ? new File(System.getProperty("user.home"))
            : base.toFile().isDirectory()
                ? base.toFile()
                : base.getParent().toFile();
    }


}
