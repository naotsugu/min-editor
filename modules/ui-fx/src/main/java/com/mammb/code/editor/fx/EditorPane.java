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

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.Session.SessionHistory;
import com.mammb.code.editor.core.editing.EditingFunctions;
import com.mammb.code.editor.fx.Command.*;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
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
    /** The session history. */
    private final SessionHistory sessionHistory = new SessionHistory();
    /** The file path property. */
    private final SimpleObjectProperty<Path> filePathProperty = new SimpleObjectProperty<>(Path.of("Untitled"));
    /** The modified property. */
    private final SimpleBooleanProperty modifiedProperty = new SimpleBooleanProperty();
    private Function<Path, EditorPane> newOpenHandler;

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
        canvas.setCursor(Cursor.TEXT);

        Font font = Font.font(context.config().fontName(), context.config().fontSize());
        draw = new FxDraw(canvas.getGraphicsContext2D(), font);
        model = EditorModel.of((path == null) ? Content.of() : Content.of(path),
            draw.fontMetrics(), screenScroll(), context);
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

    public void setNewOpenHandler(Function<Path, EditorPane> newOpenHandler) {
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
            if (e.isShortcutDown()) {
                zoom(e.getDeltaY());
            } else {
                if (e.getDeltaY() < 0) {
                    model.scrollNext((int) Math.min(5, -e.getDeltaY()));
                } else {
                    model.scrollPrev((int) Math.min(5, e.getDeltaY()));
                }
            }
            draw();
        }
    }

    private void zoom(double n) {
        draw.increaseFontSize(Math.clamp(n, -1, 1));
        model.updateFonts(draw.fontMetrics());
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
        execute(CommandKeys.of(e));
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
                if (!canDiscard()) return;
                e.setDropCompleted(true);
                e.consume();
                open(Session.of(path.get()));
                draw();
                return;
            }
            var dir = board.getFiles().stream().map(File::toPath)
                .filter(Files::isReadable).filter(Files::isDirectory).findFirst();
            if (dir.isPresent()) {
                e.setDropCompleted(true);
                e.consume();
                try (var stream = Files.list(dir.get())) {
                    // TODO replace as EditingFunctions
                    String list = stream.map(Path::toAbsolutePath)
                        .map(Path::toString).collect(Collectors.joining("\n"));
                    inputText(() -> list.isEmpty() ? dir.get().toAbsolutePath().toString() : list);
                } catch (IOException ignore) {  }
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
            execute(CommandKeys.of(Action.input(e.getCommitted())));
        } else if (!e.getComposed().isEmpty()) {
            if (!model.isImeOn()) model.imeOn();
            model.imeComposed(e.getComposed().stream()
                    .map(InputMethodTextRun::getText)
                    .collect(Collectors.joining()));
            model.draw(draw);
        } else {
            model.imeComposed("");
            model.imeOff();
        }
        draw();
    }

    private void execute(Command command) {
        switch (command) {
            case ActionCommand cmd -> model.apply(cmd.action());
            case OpenChoose _      -> openWithChooser();
            case Save _            -> save();
            case SaveAs _          -> saveAs();
            case New _             -> newEdit();
            case Palette cmd       -> showCommandPalette(cmd.initial());
            case Open cmd          -> open(Session.of(Path.of(cmd.path())));
            case Config _          -> newEdit().open(Session.of(context.config().path()));
            case FindAll cmd       -> model.apply(Action.findAll(cmd.str()));
            case GoTo cmd          -> model.apply(Action.goTo(cmd.rowNumber() - 1));
            case Wrap cmd          -> model.apply(Action.wrapLine(cmd.width()));
            case ToLowerCase _     -> model.apply(Action.replace(EditingFunctions.toLower, true));
            case ToUpperCase _     -> model.apply(Action.replace(EditingFunctions.toUpper, true));
            case Calc _            -> model.apply(Action.replace(EditingFunctions.toCalc, false));
            case Sort _            -> model.apply(Action.replace(EditingFunctions.sort, false));
            case Unique _          -> model.apply(Action.replace(EditingFunctions.unique, false));
            case Pwd _             -> inputText(() -> model.query(Query.contentPath).getParent());
            case Pwf _             -> inputText(() -> model.query(Query.contentPath));
            case Now _             -> inputText(LocalDateTime::now);
            case Today _           -> inputText(LocalDate::now);
            case Forward _         -> sessionHistory.forward().ifPresent(this::open);
            case Backward _        -> sessionHistory.backward().ifPresent(this::open);
            case ZoomIn _          -> zoom( 1);
            case ZoomOut _         -> zoom(-1);
            case Help _            -> FxDialog.about(getScene().getWindow()).showAndWait();
            case Filter cmd        -> { } // TODO impl
            case Empty _           -> { }
        }
        draw();
    }

    private void inputText(Supplier<Object> supplier) {
        try {
            model.apply(Action.input(supplier.get().toString()));
        } catch (Exception ignore) { }
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
        modifiedProperty.setValue(model.query(Query.modified));
    }

    private void openWithChooser() {
        if (!canDiscard()) return;
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
        open(Session.of(file.toPath()));
    }

    private void open(Session session) {
        final long size = fileSize(session.path());
        boolean openInBackground = size > 2_000_000;
        Content content = openInBackground ? Content.readOnlyPartOf(session.path()) : Content.of(session.path());
        model = EditorModel.of(content, draw.fontMetrics(), screenScroll(), context);
        model.setSize(getWidth(), getHeight());
        sessionHistory.push(session);
        filePathProperty.setValue(session.path());

        if (openInBackground) {
            Task<Content> task = new Task<>() {
                private final AtomicLong total = new AtomicLong(0);
                @Override protected Content call() {
                    return Content.of(session.path(), bytes -> {
                        updateProgress(total.addAndGet(bytes.length), size);
                        return !isCancelled();
                    });
                }
            };
            task.setOnSucceeded(_ -> {
                model = EditorModel.of(task.getValue(), draw.fontMetrics(), screenScroll(), context);
                model.setSize(getWidth(), getHeight());
                sessionHistory.push(session);
            });
            floatBar.handleProgress(task);
            new Thread(task).start();
        }
    }


    boolean canDiscard() {
        boolean canDiscard = true;
        if (model.query(Query.modified)) {
            var ret = FxDialog.confirmation(getScene().getWindow(),
                    "Are you sure you want to discard your changes?").showAndWait();
            canDiscard = ret.isPresent() && ret.get() == ButtonType.OK;
        }
        if (canDiscard) {
            sessionHistory.updateCurrent(model.getSession());
        }
        return canDiscard;
    }

    private void save() {
        // TODO saving large files runs in the background
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
        filePathProperty.setValue(path);
    }

    private EditorPane newEdit() {
        var handler = newOpenHandler;
        if (handler != null) {
            return handler.apply(null);
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
        return editorPane;
    }

    private void showCommandPalette(Class<? extends Command> clazz) {
        new CommandPalette(this, clazz).showAndWait().ifPresent(this::execute);
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

    public ReadOnlyObjectProperty<Path> filePathProperty() {
        return filePathProperty;
    }
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modifiedProperty;
    }

}
