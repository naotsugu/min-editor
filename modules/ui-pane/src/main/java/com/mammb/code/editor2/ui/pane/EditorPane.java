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

import com.mammb.code.editor2.ui.control.ScrollBar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleRole;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    /** The timeline. */
    private final Timeline timeline = new Timeline();

    /** The Canvas. */
    private Canvas canvas;
    /** The FX GraphicsContext. */
    private GraphicsContext gc;
    /** The editor model. */
    private EditorModel editorModel;

    private ScrollBar vScrollBar;

    /** The margin. */
    double margin = 5.5;


    /**
     * Constructor.
     * @param width the width
     * @param height the height
     */
    public EditorPane(double width, double height) {

        setWidth(width);
        setHeight(height);
        double canvasWidth = width - margin;
        double canvasHeight = height - margin;

        editorModel = new EditorModel(canvasWidth, canvasHeight);
        canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.setFocusTraversable(true);
        canvas.setAccessibleRole(AccessibleRole.TEXT_AREA);
        canvas.setLayoutX(margin);
        canvas.setLayoutY(margin);
        getChildren().add(canvas);
        initHandler();

        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.BUTT);

        timeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(500),
                e -> editorModel.tick(gc)));
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
        setOnMouseDragged(this::handleMouseDragged);
        setOnDragOver(DragDrops.dragOverHandler());
        setOnDragDropped(DragDrops.droppedHandler(this::open));
        canvas.setInputMethodRequests(inputMethodRequests());
        canvas.setOnInputMethodTextChanged(this::handleInputMethod);
        canvas.focusedProperty().addListener(this::focusChanged);

        layoutBoundsProperty().addListener(this::layoutBoundsChanged);
    }


    /**
     * Open the file content path.
     * @param path the content file path
     */
    public void open(Path path) {
        editorModel = new EditorModel(getWidth(), getHeight(), path);
        editorModel.draw(gc);
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

    public void handleMouseDragged(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            editorModel.dragged(event.getSceneX(), event.getSceneY());
        }
        editorModel.draw(gc);
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
            case HOME    -> { editorModel.moveCaretLineHome(); editorModel.draw(gc); return; }
            case END     -> { editorModel.moveCaretLineEnd(); editorModel.draw(gc); return; }
            case SELECT_ALL -> { editorModel.selectAll(); editorModel.draw(gc); return; }
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
            e.getCharacter().isEmpty()) {
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
        String ch = (ascii == 13) ? "\n" : e.getCharacter();
        editorModel.input(ch);
        editorModel.draw(gc);
    }

    public void handleInputMethod(InputMethodEvent event) {
        if (!event.getCommitted().isEmpty()) {
            editorModel.imeCommitted(event.getCommitted());
        } else if (!event.getComposed().isEmpty()) {
            if (!editorModel.isImeOn()) editorModel.imeOn(gc);
            var runs = new ArrayList<ImePallet.Run>();
            int offset = 0;
            for (var run : event.getComposed()) {
                var r = new ImePallet.Run(
                    offset,
                    run.getText(),
                    ImePallet.RunType.valueOf(run.getHighlight().name()));
                runs.add(r);
                offset += r.length();
            }
            editorModel.imeComposed(runs);
        } else {
            editorModel.imeOff();
        }
        editorModel.draw(gc);
    }

    /**
     * Called when the value of the layout changes.
     * @param observable the ObservableValue which value changed
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void layoutBoundsChanged(
            ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
        if (newValue.getWidth() > 0 && newValue.getHeight() > 0 &&
            (oldValue.getHeight() != newValue.getHeight() || oldValue.getWidth() != newValue.getWidth())) {
            setWidth(newValue.getWidth());
            setHeight(newValue.getHeight());
            double canvasWidth = newValue.getWidth() - margin;
            double canvasHeight = newValue.getHeight() - margin;
            canvas.setWidth(canvasWidth);
            canvas.setHeight(canvasHeight);
            editorModel.layoutBounds(canvasWidth, canvasHeight);
            editorModel.draw(gc);
        }
    }

    /**
     * Focus changed handler.
     * @param observable the ObservableValue which value changed
     * @param oldValue the old value
     * @param focused the new value
     */
    private void focusChanged(
            ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean focused) {
        if (focused) {
            editorModel.focusIn(gc);
            timeline.play();
        } else {
            timeline.stop();
            editorModel.focusOut(gc);
        }
    }

    /**
     * Create input method request.
     * @return the InputMethodRequests
     */
    private InputMethodRequests inputMethodRequests() {
        return new InputMethodRequests() {
            @Override
            public Point2D getTextLocation(int offset) {
                var rect = editorModel.imeOn(gc);
                return localToScreen(rect.x(), rect.y() + rect.h() + 5);
            }
            @Override
            public void cancelLatestCommittedText() {
                editorModel.imeOff();
            }
            @Override
            public int getLocationOffset(int x, int y) {
                return 0;
            }
            @Override
            public String getSelectedText() {
                return "";
            }
        };
    }

    private static File initialDirectory(Path base) {
        return (base == null)
            ? new File(System.getProperty("user.home"))
            : base.toFile().isDirectory()
                ? base.toFile()
                : base.getParent().toFile();
    }

}
