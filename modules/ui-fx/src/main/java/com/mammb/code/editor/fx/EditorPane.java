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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.editing.EditingFunctions;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    /** The context. */
    private final AppContext context;
    /** The canvas. */
    private final Canvas canvas;
    /** The draw. */
    private final Draw draw;
    /** The editor model. */
    private EditorModel model;
    /** The vertical scroll bar. */
    private final ScrollBar vScroll = new ScrollBar();
    /** The horizon scroll bar. */
    private final ScrollBar hScroll = new ScrollBar();
    /** The float bar. */
    private final FloatBar floatBar = new FloatBar(vScroll, hScroll);
    /** The file name property. */
    private final SimpleStringProperty fileNameProperty = new SimpleStringProperty("Untitled");

    private Consumer<Path> newOpenHandler;

    /**
     * Constructor.
     * @param ctx the application context
     */
    public EditorPane(AppContext ctx) {
        this(null, ctx);
    }

    /**
     * Constructor.
     * @param path the path of content
     * @param ctx the application context
     */
    public EditorPane(Path path, AppContext ctx) {
        context = ctx;
        canvas = new Canvas();
        canvas.setManaged(false);
        canvas.setFocusTraversable(true);
        draw = new FxDraw(canvas.getGraphicsContext2D());
        model = (path == null)
                ? EditorModel.of(Content.of(), draw.fontMetrics(), screenScroll())
                : EditorModel.of(Content.of(path), draw.fontMetrics(), screenScroll());
        vScroll.setOrientation(Orientation.VERTICAL);
        hScroll.setOrientation(Orientation.HORIZONTAL);
        StackPane.setAlignment(vScroll, Pos.TOP_RIGHT);
        StackPane.setAlignment(hScroll, Pos.BOTTOM_LEFT);
        getChildren().addAll(canvas, vScroll, hScroll, floatBar);

        layoutBoundsProperty().addListener(this::handleLayoutBoundsChanged);
        setOnScroll(this::handleScroll);
        setOnMouseClicked(this::handleMouseClicked);
        setOnMouseDragged(this::handleMouseDragged);
        setOnKeyPressed(this::handleKeyAction);
        setOnKeyTyped(this::handleKeyAction);
        setOnDragDetected(this::handleDragDetect);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);

        vScroll.valueProperty().addListener(this::handleVerticalScroll);
        hScroll.valueProperty().addListener(this::handleHorizontalScroll);
        canvas.setInputMethodRequests(inputMethodRequests());
        canvas.setOnInputMethodTextChanged(this::handleInputMethodTextChanged);
        canvas.focusedProperty().addListener((_, _, n) -> {
            model.setCaretVisible(n);
            draw();
        });
    }

    public ReadOnlyStringProperty fileNameProperty() {
        return fileNameProperty;
    }

    public void setNewOpenHandler(Consumer<Path> newOpenHandler) {
        this.newOpenHandler = newOpenHandler;
    }

    public void focus() {
        canvas.requestFocus();
    }

    private void handleLayoutBoundsChanged(
            ObservableValue<? extends Bounds> ob, Bounds o, Bounds n) {
        canvas.setWidth(n.getWidth());
        canvas.setHeight(n.getHeight());
        model.setSize(n.getWidth(), n.getHeight());
        draw();
    }

    private void handleScroll(ScrollEvent e) {
        if (e.getEventType() == ScrollEvent.SCROLL && e.getDeltaY() != 0) {
            if (e.getDeltaY() < 0) {
                model.scrollNext((int) Math.min(5, Math.abs(e.getDeltaY())));
            } else {
                model.scrollPrev((int) Math.min(5, e.getDeltaY()));
            }
            draw();
        }
    }

    private void handleMouseClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && e.getTarget() == canvas) {
            switch (e.getClickCount()) {
                case 1 -> {
                    if (e.isShortcutDown()) {
                        model.ctrlClick(e.getX(), e.getY());
                    } else {
                        model.click(e.getX(), e.getY(), false);
                    }
                }
                case 2 -> model.clickDouble(e.getX(), e.getY());
                case 3 -> model.clickTriple(e.getX(), e.getY());
            }
            draw();
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            model.moveDragged(e.getX(), e.getY());
            model.draw(draw);
        }
    }

    private void handleKeyAction(KeyEvent e) {
        execute(FxActions.of(e));
    }

    private void handleDragDetect(MouseEvent e) {
    }

    private void handleDragOver(DragEvent e) {
        if (e.getDragboard().hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
    }

    private void handleDragDropped(DragEvent e) {
        Dragboard board = e.getDragboard();
        if (board.hasFiles()) {
            var path = board.getFiles().stream().map(File::toPath)
                    .filter(Files::isReadable).filter(Files::isRegularFile).findFirst();
            if (path.isPresent()) {
                if (!canDiscardCurrent()) return;
                e.setDropCompleted(true);
                e.consume();
                open(path.get());
                draw();
                return;
            }
            var dir = board.getFiles().stream().map(File::toPath)
                .filter(Files::isReadable).filter(Files::isDirectory).findFirst();
            if (dir.isPresent()) {
                e.setDropCompleted(true);
                e.consume();
                try (var stream = Files.list(dir.get())) {
                    String list = stream.map(Path::toAbsolutePath)
                        .map(Path::toString).collect(Collectors.joining("\n"));
                    model.input(list.isEmpty() ? dir.get().toAbsolutePath().toString() : list);
                    draw();
                } catch (IOException ignore) {
                }
                return;
            }
        }
        e.setDropCompleted(false);
    }

    private void handleVerticalScroll(ObservableValue<? extends Number> ob, Number o, Number n) {
        model.scrollAt(n.intValue());
        draw();
    }

    private void handleHorizontalScroll(ObservableValue<? extends Number> ob, Number o, Number n) {
        model.scrollX(n.doubleValue());
        draw();
    }

    private void handleInputMethodTextChanged(InputMethodEvent e) {
        if (!e.getCommitted().isEmpty()) {
            model.imeOff();
            execute(Action.of(Action.Type.TYPED, e.getCommitted()));
        } else if (!e.getComposed().isEmpty()) {
            if (!model.isImeOn()) model.imeOn();
            model.inputImeComposed(e.getComposed().stream()
                    .map(InputMethodTextRun::getText)
                    .collect(Collectors.joining()));
            model.draw(draw);
        } else {
            model.inputImeComposed("");
            model.imeOff();
        }
        draw();
    }

    private Action execute(Action action) {

        if (model.isImeOn()) return Action.EMPTY;

        switch (action.type()) {
            case TYPED -> model.input(action.attr());
            case DELETE -> model.delete();
            case BACK_SPACE -> model.backspace();
            case UNDO -> model.undo();
            case REDO -> model.redo();
            case HOME -> model.moveCaretHome(false);
            case END -> model.moveCaretEnd(false);
            case CARET_RIGHT -> model.moveCaretRight(false);
            case CARET_LEFT -> model.moveCaretLeft(false);
            case CARET_UP -> model.moveCaretUp(false);
            case CARET_DOWN -> model.moveCaretDown(false);
            case SELECT_CARET_RIGHT -> model.moveCaretRight(true);
            case SELECT_CARET_LEFT -> model.moveCaretLeft(true);
            case SELECT_CARET_UP -> model.moveCaretUp(true);
            case SELECT_CARET_DOWN -> model.moveCaretDown(true);
            case SELECT_HOME -> model.moveCaretHome(true);
            case SELECT_END -> model.moveCaretEnd(true);
            case SELECT_ALL -> model.selectAll();
            case PAGE_UP -> model.moveCaretPageUp(false);
            case PAGE_DOWN -> model.moveCaretPageDown(false);
            case SELECT_PAGE_UP -> model.moveCaretPageUp(true);
            case SELECT_PAGE_DOWN -> model.moveCaretPageDown(true);
            case COPY -> model.copyToClipboard(FxClipboard.instance);
            case CUT -> model.cutToClipboard(FxClipboard.instance);
            case PASTE -> model.pasteFromClipboard(FxClipboard.instance);
            case ESC -> model.escape();
            case OPEN -> openWithChooser();
            case SAVE -> save();
            case SAVE_AS -> saveAs();
            case NEW -> newEdit();
            case FIND -> showCommandPalette(CommandPalette.CmdType.findAll);
            case COMMAND_PALETTE -> showCommandPalette(null);
            case INDENT -> model.replace(EditingFunctions.indent, true);
            case UNINDENT -> model.replace(EditingFunctions.unindent, true);
            case WRAP -> model.wrap(model.query(Query.widthAsCharacters) - 2);
        }

        if (action.type().syncCaret()) {
            model.scrollToCaret();
        }
        draw();
        return action;
    }

    private ScreenScroll screenScroll() {
        return new ScreenScroll() {
            @Override
            public void vertical(int min, int max, int val, int len) {
                vScroll.setMin(min);
                vScroll.setMax(max);
                vScroll.setValue(val);
                vScroll.setVisibleAmount(len);
            }
            @Override
            public void horizontal(double min, double max, double val, double len) {
                hScroll.setMin(min);
                hScroll.setMax(max);
                hScroll.setValue(val);
                hScroll.setVisibleAmount(len);
                hScroll.setVisible(max > len);
            }
            @Override
            public double xVal() {
                return hScroll.getValue();
            }
        };
    }

    private void draw() {
        model.draw(draw);
        Point p = model.query(Query.caretPoint);
        floatBar.setText(
            p.row() + 1 + ":" + p.col(),
            model.query(Query.rowEndingSymbol),
            model.query(Query.charsetSymbol) + ((model.query(Query.bom).length > 0) ? "(BOM)" : ""));
        var fileName = model.path().map(Path::getFileName).map(Path::toString).orElse("Untitled");
        fileNameProperty.setValue((model.query(Query.modified) ? "*" : "") + fileName);
    }

    private void openWithChooser() {
        if (!canDiscardCurrent()) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Select file...");
        if (model.path().isPresent()) {
            fc.setInitialDirectory(
                    model.path().get().toAbsolutePath().getParent().toFile());
        } else {
            fc.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
        }
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file == null) return;
        open(file.toPath());
    }

    private void open(Path path) {
        final long size = fileSize(path);
        boolean openInBackground = size > 2_000_000;
        Content content = openInBackground ? Content.readOnlyPartOf(path) : Content.of(path);
        model = EditorModel.of(content, draw.fontMetrics(), screenScroll());
        model.setSize(getWidth(), getHeight());
        fileNameProperty.setValue(path.getFileName().toString());

        if (openInBackground) {
            Task<Content> task = new Task<>() {
                private final AtomicLong total = new AtomicLong(0);
                @Override protected Content call() {
                    return Content.of(path, bytes -> {
                        updateProgress(total.addAndGet(bytes.length), size);
                        return !isCancelled();
                    });
                }
            };
            task.setOnSucceeded(_ -> {
                model = EditorModel.of(task.getValue(), draw.fontMetrics(), screenScroll());
                model.setSize(getWidth(), getHeight());
            });
            floatBar.handleProgress(task);
            new Thread(task).start();
        }
    }

    boolean canDiscardCurrent() {
        if (model.query(Query.modified)) {
            var result = FxDialog.confirmation(getScene().getWindow(),
                    "Are you sure you want to discard your changes?").showAndWait();
            return (result.isPresent() && result.get() == ButtonType.OK);
        } else {
            return true;
        }
    }

    private void save() {
        if (model.path().isPresent()) {
            model.save(model.path().get());
        } else {
            saveAs();
        }
    }

    private void saveAs() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save As...");
        fc.setInitialDirectory(model.path().isPresent()
                ? model.path().get().toAbsolutePath().getParent().toFile()
                : Path.of(System.getProperty("user.home")).toFile());
        File file = fc.showSaveDialog(getScene().getWindow());
        if (file == null) return;
        Path path = file.toPath();
        model.save(path);
        fileNameProperty.setValue(path.getFileName().toString());
    }

    private void newEdit() {
        var handler = newOpenHandler;
        if (handler != null) {
            handler.accept(null);
            return;
        }
        Stage current = (Stage) getScene().getWindow();
        Stage stage = new Stage();
        stage.setX(current.getX() + (current.isFullScreen() ? 0 : 15));
        stage.setY(current.getY() + (current.isFullScreen() ? 0 : 15));
        var editorPane = new EditorPane(context);
        Scene scene = new Scene(editorPane, current.getWidth(), current.getHeight());
        scene.getStylesheets().addAll(getScene().getStylesheets());
        stage.setScene(scene);
        stage.setTitle(Version.appName);
        stage.show();
    }

    private void showCommandPalette(CommandPalette.CmdType init) {
        var cp = new CommandPalette(this, init);
        var command = cp.showAndWait();
        command.ifPresent(c -> {
            switch (c.type()) {
                case findAll     -> model.findAll(c.args()[0]);
                case goTo        -> model.moveTo(c.arg0AsInt() - 1);
                case wrap        -> model.wrap(c.arg0AsInt());
                case toLowerCase -> model.replace(EditingFunctions.toLower, true);
                case toUpperCase -> model.replace(EditingFunctions.toUpper, true);
                case calc        -> model.replace(EditingFunctions.toCalc, false);
                case sort        -> model.replace(EditingFunctions.sort, false);
                case unique      -> model.replace(EditingFunctions.unique, false);
                case pwd         -> model.input(stringify(() -> model.query(Query.contentPath).getParent().toString()));
                case pwf         -> model.input(stringify(() -> model.query(Query.contentPath).toString()));
                case now         -> model.input(stringify(() -> LocalDateTime.now().toString()));
                case today       -> model.input(stringify(() -> LocalDate.now().toString()));
                case filter      -> { }
                case null        -> { }
                default -> { }
            }
        });
    }

    private InputMethodRequests inputMethodRequests() {
        return new InputMethodRequests() {
            @Override
            public Point2D getTextLocation(int i) {
                return model.imeLoc()
                        .map(loc -> canvas.localToScreen(loc.x(), loc.y()))
                        .orElse(null);
            }
            @Override
            public void cancelLatestCommittedText() {
                model.imeOff();
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

    private long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (Exception ignore) { }
        return 0;
    }

    AppContext context() {
        return context;
    }

    private static String stringify(Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return "";
        }
    }

}
