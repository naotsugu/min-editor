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
import com.mammb.code.editor.core.HoverOn;
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
import java.util.function.Consumer;
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

    private Consumer<EditorPane> closeListener;

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
        canvas.setOnMouseMoved(this::handleMouseMoved);

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
        setOnMousePressed(this::handleMousePressed);
        setOnMouseClicked(this::handleMouseClicked);
        setOnMouseDragged(this::handleMouseDragged);
        setOnKeyPressed(this::handleKeyAction);
        setOnKeyTyped(this::handleKeyAction);
        setOnDragDetected(this::handleDragDetect);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        // TODO context menu

        vScroll.valueProperty().addListener(this::handleVerticalScroll);
        hScroll.valueProperty().addListener(this::handleHorizontalScroll);
        canvas.setInputMethodRequests(inputMethodRequests());
        canvas.setOnInputMethodTextChanged(this::handleInputMethodTextChanged);
        canvas.focusedProperty().addListener((ob, _, n) -> {
            model.setCaretVisible(n);
            paint(); // TODO only caret draw
        });
        if (path != null) filePathProperty.setValue(path);
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
        paint();
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
            paint();
        }
    }

    private void zoom(double n) {
        draw.increaseFontSize(Math.clamp(n, -1, 1));
        model.updateFonts(draw.fontMetrics());
    }

    private void handleMouseMoved(MouseEvent e) {
        switch (model.hoverOn(e.getX(), e.getY())) {
            case HoverOn.GarterRegion _ -> canvas.setCursor(Cursor.DEFAULT);
            case null, default -> canvas.setCursor(Cursor.TEXT);
        }
    }
    private void handleMousePressed(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            model.mousePressed(e.getX(), e.getY());
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
            paint();
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            model.moveDragged(e.getX(), e.getY());
            model.paint(draw);
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
            var paths = board.getFiles().stream().map(File::toPath).toList();
            var path = paths.stream().filter(Files::isReadable)
                .filter(Files::isRegularFile).findFirst();
            if (path.isPresent()) {
                if (!canDiscard()) return;
                e.setDropCompleted(true);
                e.consume();
                open(Session.of(path.get()));
                paint();
                return;
            }
            var list = EditingFunctions.list.apply(paths);
            if (!list.isEmpty()) {
                e.setDropCompleted(true);
                e.consume();
                inputText(() -> list);
                paint();
                return;
            }
        }
        e.setDropCompleted(false);
    }

    private void handleVerticalScroll(ObservableValue<? extends Number> ob, Number o, Number n) {
        model.scrollAt(n.intValue());
        paint();
    }

    private void handleHorizontalScroll(ObservableValue<? extends Number> ob, Number o, Number n) {
        model.scrollX(n.doubleValue());
        paint();
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
            model.paint(draw);
        } else {
            model.imeComposed("");
            model.imeOff();
        }
        paint();
    }

    private void execute(Command command) {
        switch (command) {
            case ActionCommand cmd  -> model.apply(cmd.action());
            case OpenChoose _       -> openWithChooser();
            case Save _             -> save();
            case SaveAs _           -> saveAs();
            case New _              -> newEdit();
            case TabClose _         -> { if (closeListener != null) closeListener.accept(this); }
            case Palette cmd        -> showCommandPalette(cmd.initial());
            case Open cmd           -> open(cmd.path());
            case Config _           -> newEdit().open(Session.of(context.config().path()));
            case FindNext cmd       -> model.apply(Action.findNext(cmd.str(), cmd.caseSensitive()));
            case FindPrev cmd       -> model.apply(Action.findPrev(cmd.str(), cmd.caseSensitive()));
            case FindAll cmd        -> model.apply(Action.findAll(cmd.str(), cmd.caseSensitive()));
            case FindNextRegex cmd  -> model.apply(Action.findNextRegex(cmd.str()));
            case FindPrevRegex cmd  -> model.apply(Action.findPrevRegex(cmd.str()));
            case FindAllRegex cmd   -> model.apply(Action.findAllRegex(cmd.str()));
            case Select cmd         -> model.apply(Action.select(cmd.str(), cmd.caseSensitive()));
            case SelectRegex cmd    -> model.apply(Action.selectRegex(cmd.str()));
            case GoTo cmd           -> model.apply(Action.goTo(cmd.rowNumber() - 1));
            case WrapLine cmd       -> model.apply(Action.wrapLine(cmd.width()));
            case ToggleLayout _     -> model.apply(Action.toggleLayout());
            case ToLowerCase _      -> model.apply(Action.replace(EditingFunctions.toLower, true));
            case ToUpperCase _      -> model.apply(Action.replace(EditingFunctions.toUpper, true));
            case IndentParen _      -> model.apply(Action.replace(EditingFunctions.toIndentParen, false));
            case IndentCurlyBrace _ -> model.apply(Action.replace(EditingFunctions.toIndentCurlyBrace, false));
            case Calc _             -> model.apply(Action.replace(EditingFunctions.toCalc, false));
            case Sort _             -> model.apply(Action.replace(EditingFunctions.sort, false));
            case Unique _           -> model.apply(Action.replace(EditingFunctions.unique, false));
            case DecToHex _         -> model.apply(Action.replace(EditingFunctions.decToHex, true));
            case DecToBin _         -> model.apply(Action.replace(EditingFunctions.decToBin, true));
            case HexToBin _         -> model.apply(Action.replace(EditingFunctions.hexToBin, true));
            case HexToDec _         -> model.apply(Action.replace(EditingFunctions.hexToDec, true));
            case BinToHex _         -> model.apply(Action.replace(EditingFunctions.binToHex, true));
            case BinToDec _         -> model.apply(Action.replace(EditingFunctions.binToDec, true));
            case Pwd _              -> inputText(() -> model.query(Query.contentPath).getParent());
            case Pwf _              -> inputText(() -> model.query(Query.contentPath));
            case Now _              -> inputText(LocalDateTime::now);
            case Today _            -> inputText(LocalDate::now);
            case Forward _          -> sessionHistory.forward().ifPresent(this::open);
            case Backward _         -> sessionHistory.backward().ifPresent(this::open);
            case ZoomIn _           -> zoom( 1);
            case ZoomOut _          -> zoom(-1);
            case Help _             -> FxDialog.about(getScene().getWindow(), context).showAndWait();
            case Filter cmd         -> { } // TODO impl
            case Empty _            -> { }
        }
        paint();
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

    private void paint() {
        model.paint(draw);
        Point p = model.query(Query.caretPoint);
        int selectedCounts = model.query(Query.selectedCounts);
        int foundCounts = model.query(Query.foundCounts);
        floatBar.setText(
            selectedCounts > 0 ? selectedCounts + " selected" : "",
            foundCounts > 0 ? foundCounts + " found" : "",
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

    void open(String pathString) {
        if (!canDiscard()) return;
        var path = Path.of(pathString);
        if (!Files.exists(path) || !Files.isReadable(path)) return;
        if (Files.isRegularFile(path)) {
            open(Session.of(path));
        } else if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                String list = stream.map(Path::toAbsolutePath)
                    .map(Path::toString).collect(Collectors.joining("\n"));
                newEdit().inputText(() -> list.isEmpty() ? path.toAbsolutePath().toString() : list);
            } catch (IOException ignore) {  }
        }
    }

    private void open(Session session) {

        final long size = fileSize(session.path());
        boolean openInBackground = size > 2_000_000;

        // save previous session
        sessionHistory.push(model.getSession());

        model = openInBackground
            ? EditorModel.of(Content.readOnlyPartOf(session.path()), draw.fontMetrics(), screenScroll(), context)
            : EditorModel.of(session, draw.fontMetrics(), screenScroll(), context, getWidth(), getHeight());
        model.setSize(getWidth(), getHeight());
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
                System.gc();
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

    void setCloseListener(Consumer<EditorPane> closeListener) {
        this.closeListener = closeListener;
    }

}
