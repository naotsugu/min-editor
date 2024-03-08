/*
 * Copyright 2023-2024 the original author or authors.
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

import com.mammb.code.editor.ui.model.ModelQuery;
import com.mammb.code.editor.ui.model.editing.Editing;
import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.ScreenPoint;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Cursor;
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
import java.nio.file.Files;
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
    /** The editor up call. */
    private final EditorUpCall upCall;
    /** The canvas. */
    private final Canvas canvas;
    /** The graphics context. */
    private final GraphicsContext gc;
    /** The vertical scroll bar for line scroll. */
    private final VScrollBar vScrollBar;
    /** The horizontal scroll bar for line scroll. */
    private final HScrollBar hScrollBar;
    /** The status bar. */
    private final StatusBar statusBar;
    /** The margin. */
    private final double margin = 5.5;

    /** The editor model. */
    private EditorModel model;



    /**
     * Constructor.
     * @param context the context
     * @param upCall the editor up call
     */
    public EditorPane(Context context, EditorUpCall upCall) {

        this.context = context;
        this.upCall = (upCall == null) ? EditorUpCall.empty() : upCall;

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
        getChildren().addAll(statusBar, vScrollBar, hScrollBar);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), this::handleTick));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        initHandler();
        initModelHandler();

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
     * Initialize Model handler.
     */
    private void initModelHandler() {
        var stateHandler = model.stateChange();
        statusBar.bind(stateHandler);
        stateHandler.addContentModifyChanged(c -> upCall.contentModified(session(), c));
    }


    /**
     * Get the editor down call.
     * @return the editor down call
     */
    public EditorDownCall downCall() {
        return new EditorDownCall() {
            @Override public void requestPathChange(Session session) {
                open(session);
            }
            @Override public void requestFind(String regexp, boolean forward) {
                find(regexp, forward);
            }
            @Override public void requestFocus() {
                canvas.requestFocus();
            }
            @Override public <T> void requestQuery(Query<T> query) { query.on(model.query(query.modelQuery())); model.selectOff(); }
        };
    }


    /**
     * Open the file content path.
     * @param path the content file path
     */
    public void open(Path path) {
        if (!Files.isReadable(path)) {
            return;
        }
        if (Files.isDirectory(path)) {

        } else {
            open(Session.of(path));
        }
    }


    /**
     * Open the session.
     * @param session the session
     */
    private void open(Session session) {

        // save the session before changes
        var prev = session();

        FileAction.of(this, model).open(session.path(), e -> {
            this.handleModelCreated(e);
            if (!session.isOriginPoint()) {
                var sp = new ScreenPoint(session.row(), session.caretIndex());
                withDraw(() -> model.apply(sp));
            }
            upCall.pathChanged(session(), prev);
        });
    }


    /**
     * Get the current session.
     * @return the current session
     */
    private Session session() {
        var screenPoint = model.screenPoint();
        return Session.of(model.query(ModelQuery.contentPath), screenPoint.row(), screenPoint.caretIndex());
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
                case 1 -> model.click(e.getX(), e.getY(), e.isShortcutDown());
                case 2 -> model.clickDouble(e.getX(), e.getY());
                case 3 -> model.clickTriple(e.getX(), e.getY());
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
            model.dragged(e.getX(), e.getY());
        }
        model.draw(gc);
    }


    /**
     * Key pressed handler.
     * @param e the key event
     */
    private void handleKeyPressed(KeyEvent e) {

        final Keys.Action keyAction = Keys.asAction(e);
        final boolean withSelect = e.isShiftDown();

        switch (keyAction) {
            case CARET_RIGHT -> aroundEdit(model::moveCaretRight, withSelect);
            case CARET_LEFT  -> aroundEdit(model::moveCaretLeft, withSelect);
            case CARET_UP    -> aroundEdit(model::moveCaretUp, withSelect);
            case CARET_DOWN  -> aroundEdit(model::moveCaretDown, withSelect);
            case PAGE_UP     -> aroundEdit(model::pageUp, withSelect);
            case PAGE_DOWN   -> aroundEdit(model::pageDown, withSelect);
            case DELETE      -> withDraw(model::delete);
            case BACK_SPACE  -> withDraw(model::backspace);
            case ESCAPE      -> withDraw(model::clear);
            case OPEN        -> FileAction.of(this, model).open(createOpenHandler());
            case SAVE        -> FileAction.of(this, model).save(createSaveHandler());
            case SAVE_AS     -> FileAction.of(this, model).saveAs(createSaveHandler());
            case COPY        -> model.copyToClipboard();
            case PASTE       -> withDraw(model::pasteFromClipboard);
            case CUT         -> withDraw(model::cutToClipboard);
            case UNDO        -> withDraw(model::undo);
            case REDO        -> withDraw(model::redo);
            case SELECT_ALL  -> withDraw(model::selectAll);
            case WRAP        -> withDraw(model::toggleWrap);
            case HOME        -> aroundEdit(model::moveCaretLineHome, withSelect);
            case END         -> aroundEdit(model::moveCaretLineEnd, withSelect);
            case UPPER,LOWER -> withDraw(() -> model.applyEditing(Editing.upperCase()));
            case SCROLL_UP   -> withDraw(() -> model.scrollPrev(1));
            case SCROLL_DOWN -> withDraw(() -> model.scrollNext(1));
            case HEX         -> withDraw(() -> model.applyEditing(Editing.hexCase()));
            case CALC        -> withDraw(() -> model.applyEditing(Editing.calcCase()));
            case DEBUG       -> debug();
            //case NEW       -> newPane();
            default          -> { }
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
            e.getCharacter().isEmpty()  || !(e.getSource() instanceof EditorPane)) {
            return;
        }

        int ascii = e.getCharacter().getBytes()[0];
        if (ascii < 32 || ascii == 127) { // 127:DEL

            if (ascii == 9 || ascii == 25) { // 9:TAB 25:ME(shift+tab)
                if (model.peekSelection(t -> t.contains("\n"))) {
                    withDraw(() -> {
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
        withDraw(() -> model.applyEditing(Editing.keyTypedSteal(ch)));
    }


    /**
     * Tick handler.
     * @param e the action event
     */
    private void handleTick(ActionEvent e) {
        model.tick(gc);
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
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            pause.setOnFinished(e -> model.showCaret(gc));
            pause.play();
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


    private EventHandler<WorkerStateEvent> createOpenHandler() {
        var prev = session();
        return (WorkerStateEvent e) -> {
            this.handleModelCreated(e);
            upCall.pathChanged(session(), prev);
        };
    }


    private EventHandler<WorkerStateEvent> createSaveHandler() {
        var prev = session();
        return (WorkerStateEvent e) -> {
            this.handleModelCreated(e);
            if (!prev.isOriginPoint()) {
                var sp = new ScreenPoint(prev.row(), prev.caretIndex());
                withDraw(() -> model.apply(sp));
            }
            var curr = session();
            if (prev.isEmptyPath() || !prev.path().equals(curr.path())) {
                upCall.pathChanged(curr, prev);
            }
        };
    }

    /**
     * Model update task handler.
     * @param e the worker state event
     */
    private void handleModelCreated(WorkerStateEvent e) {
        model = (EditorModel) e.getSource().getValue();
        initModelHandler();
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


    /**
     * Find action from the down call.
     * @param regexp the regexp string
     * @param forward forward
     */
    private void find(String regexp, boolean forward) {
        canvas.requestFocus();
        withDraw(() -> model.findHandle().findNext(regexp, true, forward));
    }


    private void debug() {

    }


    private void withDraw(Runnable runnable) {
        timeline.stop();
        model.showCaret(gc);
        runnable.run();
        model.draw(gc);
        timeline.play();
    }


    private void aroundEdit(Runnable edit, boolean withSelect) {
        withDraw(() -> {
            if (withSelect) {
                model.selectOn();
            } else {
                model.selectOff();
            }
            edit.run();
        });
    }

}
