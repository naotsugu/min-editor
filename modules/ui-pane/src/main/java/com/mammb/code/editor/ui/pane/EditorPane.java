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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.nio.file.Path;

/**
 * EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    /** The timeline for caret blink. */
    private final Timeline timeline = new Timeline();
    /** The Context. */
    private final Context context;

    /** The canvas. */
    private final Canvas canvas;
    /** The graphics context. */
    private GraphicsContext gc;
    /** The editor model. */
    private EditorModel model;
    /** The vertical scroll bar for line scroll. */
    private final VScrollBar vScrollBar;
    /** The horizontal scroll bar for line scroll. */
    private final HScrollBar hScrollBar;
    /** The status bar. */
    private final StatusBar statusBar;

    /** The margin. */
    private final double margin = 5.5;


    /**
     * Constructor.
     * @param context the context
     */
    public EditorPane(Context context) {

        this.context = context;
        setCursor(Cursor.TEXT);
        setWidth(context.regionWidth());
        setHeight(context.regionHeight());
        setBackground(new Background(new BackgroundFill(
            Color.web(context.preference().bgColor()),
            CornerRadii.EMPTY, Insets.EMPTY)));
        setFocusTraversable(false);

        double canvasWidth = context.regionWidth() - margin;
        double canvasHeight = context.regionHeight() - margin;

        canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.setFocusTraversable(true);
        canvas.setAccessibleRole(AccessibleRole.TEXT_AREA);
        canvas.setLayoutX(margin);
        canvas.setLayoutY(margin);
        getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.BUTT);

        vScrollBar = new VScrollBar(Color.web(context.preference().fgColor()));
        StackPane.setAlignment(vScrollBar, Pos.CENTER_RIGHT);
        hScrollBar = new HScrollBar(Color.web(context.preference().fgColor()));
        StackPane.setAlignment(hScrollBar, Pos.BOTTOM_LEFT);

        model = EditorModel.of(context, canvasWidth, canvasHeight, vScrollBar, hScrollBar);
        canvas.setInputMethodRequests(inputMethodRequests());
        statusBar = new StatusBar(context);
        statusBar.bind(model.stateChange());
        getChildren().addAll(statusBar, vScrollBar, hScrollBar);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), e -> model.tick(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        initHandler();

    }


    /**
     * Initialize handler.
     */
    private void initHandler() {

        setOnKeyPressed(this::handleKeyPressed);
        setOnKeyTyped(this::handleKeyTyped);
        setOnScroll(this::handleScroll);
        setOnMouseMoved(this::handleMouseMoved);
        setOnMouseClicked(this::handleMouseClicked);
        setOnMouseDragged(this::handleMouseDragged);
        setOnDragOver(DragDrops.dragOverHandler());
        setOnDragDropped(DragDrops.droppedHandler(this::open));
        layoutBoundsProperty().addListener(this::layoutBoundsChanged);

        canvas.setOnInputMethodTextChanged(this::handleInputMethod);
        canvas.focusedProperty().addListener(this::focusChanged);

        vScrollBar.setOnScrolled(this::handleVScrolled);
        hScrollBar.setOnScrolled(this::handleHScrolled);

    }


    /**
     * Open the file content path.
     * @param path the content file path
     */
    public void open(Path path) {
        FileAction.of(this, model).open(path, this::handleModelCreated);
    }


    /**
     * Scroll event handler.
     * @param e the scroll event
     */
    private void handleScroll(ScrollEvent e) {
        if (e.getEventType() == ScrollEvent.SCROLL && e.getDeltaY() != 0) {
            if (e.getDeltaY() > 0) {
                model.scrollPrev(Math.min((int) e.getDeltaY(), 3));
            } else {
                model.scrollNext(Math.min(Math.abs((int) e.getDeltaY()), 3));
            }
            model.draw(gc);
        }
    }


    /**
     * Mouse moved handler.
     * @param e the mouse event
     */
    private void handleMouseMoved(MouseEvent e) {
        if (e.getY() > 0 && e.getX() > model.textAreaRect().x()) {
            setCursor(Cursor.TEXT);
        } else {
            setCursor(Cursor.DEFAULT);
        }
    }


    /**
     * Mouse clicked handler.
     * @param e the mouse event
     */
    private void handleMouseClicked(MouseEvent e) {
        canvas.requestFocus();
        if (e.getButton() == MouseButton.PRIMARY && e.getTarget() == canvas) {
            switch (e.getClickCount()) {
                case 1 -> model.click(e.getSceneX(), e.getSceneY());
                case 2 -> model.clickDouble(e.getSceneX(), e.getSceneY());
                case 3 -> model.clickTriple(e.getSceneX(), e.getSceneY());
            }
            model.draw(gc);
        }
    }


    /**
     * Mouse dragged handler.
     * @param e the mouse event
     */
    private void handleMouseDragged(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && e.getTarget() == canvas) {
            model.dragged(e.getSceneX(), e.getSceneY());
        }
        model.draw(gc);
    }


    /**
     * Key pressed handler.
     * @param e the key event
     */
    private void handleKeyPressed(KeyEvent e) {

        final Keys.Action action = Keys.asAction(e);
        final boolean withSelect = e.isShiftDown();

        if (action != Keys.Action.EMPTY) {
            switch (action) {
                case OPEN       -> FileAction.of(this, model).open(this::handleModelCreated);
                case SAVE       -> FileAction.of(this, model).save(this::handleModelCreated);
                case SAVE_AS    -> FileAction.of(this, model).saveAs(this::handleModelCreated);
                case COPY       -> model.copyToClipboard();
                case PASTE      -> aroundEdit(model::pasteFromClipboard);
                case CUT        -> aroundEdit(model::cutToClipboard);
                case UNDO       -> aroundEdit(model::undo);
                case REDO       -> aroundEdit(model::redo);
                case SELECT_ALL -> aroundEdit(model::selectAll);
                case WRAP       -> aroundEdit(model::toggleWrap);
                case HOME       -> aroundEdit(model::moveCaretLineHome, withSelect);
                case END        -> aroundEdit(model::moveCaretLineEnd, withSelect);
                //case NEW        -> newPane();
                case DEBUG      -> debug();
            }
            return;
        }

        switch (e.getCode()) {
            case RIGHT      -> aroundEdit(model::moveCaretRight, withSelect);
            case LEFT       -> aroundEdit(model::moveCaretLeft, withSelect);
            case UP         -> aroundEdit(model::moveCaretUp, withSelect);
            case DOWN       -> aroundEdit(model::moveCaretDown, withSelect);
            case HOME       -> aroundEdit(model::moveCaretLineHome, withSelect);
            case END        -> aroundEdit(model::moveCaretLineEnd, withSelect);
            case PAGE_UP    -> aroundEdit(model::pageUp, withSelect);
            case PAGE_DOWN  -> aroundEdit(model::pageDown, withSelect);
            case DELETE     -> aroundEdit(model::delete);
            case BACK_SPACE -> aroundEdit(model::backspace);
            case ESCAPE     -> aroundEdit(model::selectOff);
        }
    }


    /**
     * Key typed handler.
     * @param e the key event
     */
    private void handleKeyTyped(KeyEvent e) {

        if (e.getCode().isFunctionKey() || e.getCode().isNavigationKey() ||
            e.getCode().isArrowKey()    || e.getCode().isModifierKey() ||
            e.getCode().isMediaKey()    || !Keys.controlKeysFilter.test(e) ||
            e.getCharacter().isEmpty()) {
            return;
        }

        int ascii = e.getCharacter().getBytes()[0];
        if (ascii < 32 || ascii == 127) { // 127:DEL

            if (ascii == 9 || ascii == 25) { // 9:TAB 25:ME(shift+tab)
                if (model.peekSelection(t -> t.contains("\n"))) {
                    aroundEdit(() -> {
                        if (e.isShiftDown()) model.unindent();
                        else model.indent();
                    });
                    return;
                }
            }

            if (ascii != 9 && ascii != 10 && ascii != 13) { // 9:HT 10:LF 13:CR
                return;
            }
        }

        String ch = (ascii == 13) // 13:CR
            ? "\n"
            : e.getCharacter();
        aroundEdit(() -> model.input(ch));
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
            model.layoutBounds(canvasWidth, canvasHeight);
            model.draw(gc);
        }
    }


    /**
     * Focus changed handler.
     * @param observable the ObservableValue which value changed
     * @param oldValue the old value
     * @param focused the new value
     */
    private void focusChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean focused) {
        if (focused) {
            model.showCaret(gc);
            timeline.play();
        } else {
            timeline.stop();
            model.hideCaret(gc);
        }
    }


    /**
     * Vertical scrolled handler.
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void handleVScrolled(Integer oldValue, Integer newValue) {
        model.vScrolled(oldValue, newValue);
        model.draw(gc);
    }


    /**
     * Horizontal scrolled handler.
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void handleHScrolled(Double oldValue, Double newValue) {
        model.draw(gc);
    }


    /**
     * Input method handler
     * @param e the input method event
     */
    private void handleInputMethod(InputMethodEvent e) {
        ImeAction.of(gc, model).handle(e);
    }


    /**
     * Create input method request.
     * @return the InputMethodRequests
     */
    private InputMethodRequests inputMethodRequests() {
        return ImeAction.of(gc, model).createRequest(this);
    }


    /**
     * Model update task handler.
     * @param e the worker state event
     */
    private void handleModelCreated(WorkerStateEvent e) {
        model = (EditorModel) e.getSource().getValue();
        statusBar.bind(model.stateChange());
        canvas.setInputMethodRequests(inputMethodRequests());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        model.draw(gc);
        canvas.requestFocus();
    }


    /**
     * Window close request handler.
     * @param e the window event
     */
    public void handleCloseRequest(WindowEvent e) {
        if (e.getTarget() instanceof Stage stage) {
            e.consume();
            FileAction.of(this, model).confirmIfDirty(stage::close);
        }
    }


    private void debug() {
        model.findHandle().findAll("public", false);
    }


    private void newPane() {
        Stage newStage = new Stage();
        Bounds bounds = localToScreen(getBoundsInLocal());
        newStage.setX(bounds.getMinX() + 15);
        newStage.setY(bounds.getMinY() + 15);
        new EditorPane(context).showOn(newStage);
    }


    public void showOn(Stage stage) {
        Scene scene = new Scene(this, getWidth(), getHeight());
        stage.setScene(scene);
        stage.setTitle("min-editor");
        stage.setOnCloseRequest(this::handleCloseRequest);
        stage.show();
    }


    private void aroundEdit(Runnable runnable) {
        timeline.stop();
        model.showCaret(gc);
        runnable.run();
        model.draw(gc);
        timeline.play();
    }


    private void aroundEdit(Runnable edit, boolean withSelect) {
        aroundEdit(() -> {
            if (withSelect) {
                model.selectOn();
            } else {
                model.selectOff();
            }
            edit.run();
            model.selectTo();
        });
    }

}
