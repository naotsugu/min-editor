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

import com.mammb.code.editor.ui.control.HScrollBar;
import com.mammb.code.editor.ui.control.VScrollBar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
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
    /** The vertical scroll bar for line scroll. */
    private VScrollBar vScrollBar;
    /** The horizontal scroll bar for line scroll. */
    private HScrollBar hScrollBar;

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

        vScrollBar = new VScrollBar(Global.fgColor);
        StackPane.setAlignment(vScrollBar, Pos.CENTER_RIGHT);
        hScrollBar = new HScrollBar(Global.fgColor);
        StackPane.setAlignment(hScrollBar, Pos.BOTTOM_LEFT);
        getChildren().addAll(vScrollBar, hScrollBar);
        editorModel.setScroll(vScrollBar, hScrollBar);

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
        editorModel = new EditorModel(getWidth(), getHeight(), path, vScrollBar, hScrollBar);
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
        if (e.getEventType() == ScrollEvent.SCROLL && e.getDeltaY() != 0) {
            if (e.getDeltaY() > 0) {
                editorModel.scrollPrev(Math.min((int) e.getDeltaY(), 3));
            } else {
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

        final Keys.Action action = Keys.asAction(e);
        final boolean withSelect = e.isShiftDown();

        if (action != Keys.Action.EMPTY) {
            switch (action) {
                case OPEN       -> openChoose();
                case SAVE       -> editorModel.save();
                case SAVE_AS    -> saveChoose();
                case COPY       -> editorModel.copyToClipboard();
                case PASTE      -> aroundEdit(editorModel::pasteFromClipboard);
                case CUT        -> aroundEdit(editorModel::cutToClipboard);
                case UNDO       -> aroundEdit(editorModel::undo);
                case REDO       -> aroundEdit(editorModel::redo);
                case SELECT_ALL -> aroundEdit(editorModel::selectAll);
                case WRAP       -> aroundEdit(editorModel::toggleWrap);
                case HOME       -> aroundEdit(editorModel::moveCaretLineHome, withSelect);
                case END        -> aroundEdit(editorModel::moveCaretLineEnd, withSelect);
            }
            return;
        }

        switch (e.getCode()) {
            case RIGHT      -> aroundEdit(editorModel::moveCaretRight, withSelect);
            case LEFT       -> aroundEdit(editorModel::moveCaretLeft, withSelect);
            case UP         -> aroundEdit(editorModel::moveCaretUp, withSelect);
            case DOWN       -> aroundEdit(editorModel::moveCaretDown, withSelect);
            case HOME       -> aroundEdit(editorModel::moveCaretLineHome, withSelect);
            case END        -> aroundEdit(editorModel::moveCaretLineEnd, withSelect);
            case PAGE_UP    -> aroundEdit(editorModel::moveCaretPageUp, withSelect);
            case PAGE_DOWN  -> aroundEdit(editorModel::moveCaretPageDown, withSelect);
            case DELETE     -> aroundEdit(editorModel::delete);
            case BACK_SPACE -> aroundEdit(editorModel::backspace);
            case ESCAPE     -> aroundEdit(editorModel::selectOff);
        }
    }


    public void handleKeyTyped(KeyEvent e) {
        System.out.println(e);
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
        String ch = (ascii == 13)
            ? editorModel.metrics().lineEnding().str()
            : e.getCharacter();
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

    private void aroundEdit(Runnable runnable) {
        runnable.run();
        editorModel.draw(gc);
    }

    private void aroundEdit(Runnable edit, boolean withSelect) {
        aroundEdit(() -> {
            if (withSelect) {
                editorModel.selectOn();
            } else {
                editorModel.selectOff();
            }
            edit.run();
            editorModel.selectTo();
        });
    }


    private static File initialDirectory(Path base) {
        return (base == null)
            ? new File(System.getProperty("user.home"))
            : base.toFile().isDirectory()
                ? base.toFile()
                : base.getParent().toFile();
    }

}
