/*
 * Copyright 2023-2025 the original author or authors.
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

import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.HoverOn;
import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.SessionHistory;
import com.mammb.code.editor.core.editing.EditingFunctions;
import com.mammb.code.editor.fx.Command.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ContextMenuEvent;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends ContentPane {

    /** The logger. */
    private static final System.Logger log = System.getLogger(EditorPane.class.getName());
    /** The threshold. */
    private static final long BACKGROUND_THRESHOLD = 2_000_000;

    /** The context. */
    private final AppContext context;
    /** The canvas. */
    private final Canvas canvas;
    /** The draw. */
    private final Draw draw;
    /** The editor model. */
    private EditorModel model;
    /** The screen scroll. */
    private final FxScreenScroll scroll = new FxScreenScroll(new ScrollBar(), new ScrollBar());
    /** The float bar. */
    private final FloatBar floatBar = new FloatBar(scroll.vScroll(), scroll.hScroll());
    /** The session history. */
    private final SessionHistory sessionHistory = new SessionHistory();
    /** The file path property. */
    private final SimpleObjectProperty<Name> nameProperty = new SimpleObjectProperty<>(Name.EMPTY);

    private Consumer<ContentPane> closeListener;

    /**
     * Constructor.
     * @param ctx the application context
     */
    public EditorPane(AppContext ctx) {

        context = ctx;

        canvas = new Canvas();
        canvas.setManaged(false);
        canvas.setFocusTraversable(true);
        canvas.setOnMouseMoved(this::handleMouseMoved);

        Font font = Font.font(context.config().fontName(), context.config().fontSize());
        draw = new FxDraw(canvas.getGraphicsContext2D(), font);
        model = EditorModel.of(draw.fontMetrics(), scroll, context);
        scroll.vScroll().setOrientation(Orientation.VERTICAL);
        scroll.hScroll().setOrientation(Orientation.HORIZONTAL);
        StackPane.setAlignment(scroll.vScroll(), Pos.TOP_RIGHT);
        StackPane.setAlignment(scroll.hScroll(), Pos.BOTTOM_LEFT);
        getChildren().addAll(canvas, scroll.vScroll(), scroll.hScroll(), floatBar);

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
        setOnContextMenuRequested(this::handleContextMenuRequested);

        scroll.vScroll().valueProperty().addListener(this::handleVerticalScroll);
        scroll.hScroll().valueProperty().addListener(this::handleHorizontalScroll);
        canvas.setInputMethodRequests(inputMethodRequests());
        canvas.setOnInputMethodTextChanged(this::handleInputMethodTextChanged);
        canvas.focusedProperty().addListener((_, _, n) -> {
            model().setCaretVisible(n);
            paint(); // TODO only caret draw
        });
        nameProperty.setValue(model.query(Query.modelName));
    }

    public EditorPane bindLater(Session session) {
        Platform.runLater(() -> {
            if (session.hasPath()) {
                open(session);
            } else if (session.hasAltPath()) {
                model = model.with(session);
            }
            paint();
        });
        return this;
    }

    @Override
    public void focus() {
        canvas.requestFocus();
    }

    private void handleContextMenuRequested(ContextMenuEvent e) {
        var cm = new AppContextMenu(this);
        cm.show(getScene().getWindow(), e.getScreenX(), e.getScreenY());
    }

    private void handleLayoutBoundsChanged(ObservableValue<? extends Bounds> ob, Bounds o, Bounds n) {
        canvas.setWidth(n.getWidth());
        canvas.setHeight(n.getHeight());
        model().setSize(n.getWidth(), n.getHeight());
        paint();
    }

    private void handleScroll(ScrollEvent e) {
        if (e.getEventType() == ScrollEvent.SCROLL && e.getDeltaY() != 0) {
            if (e.isShortcutDown()) {
                zoom(e.getDeltaY());
            } else {
                if (e.getDeltaY() < 0) {
                    model().scrollNext((int) Math.min(5, -e.getDeltaY()));
                } else {
                    model().scrollPrev((int) Math.min(5, e.getDeltaY()));
                }
            }
            paint();
        }
    }

    private void zoom(double n) {
        draw.increaseFontSize(Math.clamp(n, -1, 1));
        model().updateFonts(draw.fontMetrics());
    }

    private EditorPane duplicate() {
        return stash().map(session -> {
            var dup = new EditorPane(context);
            dup.model = dup.model.with(session.asReadonly());
            return dup;
        }).orElse(null);
    }

    private void searchInBrowser(String query) {
        if (query == null || query.isBlank()) return;
        context.getApp().getHostServices().showDocument("https://www.google.com/search?q="
            + URLEncoder.encode(query, StandardCharsets.UTF_8));
    }

    private void translateInBrowser(String text) {
        if (text == null || text.isBlank()) return;
        context.getApp().getHostServices().showDocument("https://translate.google.com/?op=translate&text="
            + URLEncoder.encode(text, StandardCharsets.UTF_8));
    }


    private void openRight(ContentPane contentPane) {
        var container = tabContainer();
        if (container != null && contentPane != null) {
            container.parent().addRight(contentPane);
        }
    }

    private SplitTabPane.DndTabPane tabContainer() {
        Node node = getParent();
        for (;;) {
            if (node == null) return null;
            if (node instanceof SplitTabPane.DndTabPane pane) return pane;
            node = node.getParent();
        }
    }

    private void handleMouseMoved(MouseEvent e) {
        switch (model().hoverOn(e.getX(), e.getY())) {
            case HoverOn.GarterRegion _ -> canvas.setCursor(Cursor.DEFAULT);
            case null, default -> canvas.setCursor(Cursor.TEXT);
        }
    }

    private void handleMousePressed(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            model().mousePressed(e.getX(), e.getY());
        }
    }

    private void handleMouseClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && e.getTarget() == canvas) {
            switch (e.getClickCount()) {
                case 1 -> {
                    if (e.isShortcutDown()) {
                        model().ctrlClick(e.getX(), e.getY());
                    } else {
                        model().click(e.getX(), e.getY(), false);
                    }
                }
                case 2 -> model().clickDouble(e.getX(), e.getY());
                case 3 -> model().clickTriple(e.getX(), e.getY());
            }
            paint();
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            model().moveDragged(e.getX(), e.getY());
            model().paint(draw);
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
            var path = paths.stream().filter(Files::isReadableFile).findFirst();
            if (path.isPresent()) {
                if (!canClose()) return;
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
        model().scrollAt(n.intValue());
        paint();
    }

    private void handleHorizontalScroll(ObservableValue<? extends Number> ob, Number o, Number n) {
        model().scrollX(n.doubleValue());
        paint();
    }

    private void handleInputMethodTextChanged(InputMethodEvent e) {
        if (!e.getCommitted().isEmpty()) {
            model().imeOff();
            execute(CommandKeys.of(Action.input(e.getCommitted())));
        } else if (!e.getComposed().isEmpty()) {
            if (!model().isImeOn()) model().imeOn();
            model().imeComposed(e.getComposed().stream()
                    .map(InputMethodTextRun::getText)
                    .collect(Collectors.joining()));
        } else {
            model().imeComposed("");
            model().imeOff();
        }
        paint();
    }

    void execute(Command command) {
        switch (command) {
            case ActionCommand cmd    -> model().apply(cmd.action());
            case OpenChoose _         -> openWithChooser();
            case Save _               -> save();
            case SaveAs _             -> saveAs();
            case New _                -> newEdit();
            case Reload _             -> reload();
            case TabClose _           -> { if (closeListener != null) closeListener.accept(this); }
            case Palette cmd          -> showCommandPalette(cmd.initial());
            case Open cmd             -> open(Path.of(cmd.path()));
            case Config _             -> newEdit().open(Session.of(context.config().path()));
            case FindNext cmd         -> apply(Action.findNext(cmd.str(), cmd.caseSensitive()));
            case FindPrev cmd         -> apply(Action.findPrev(cmd.str(), cmd.caseSensitive()));
            case FindAll cmd          -> apply(Action.findAll(cmd.str(), cmd.caseSensitive()));
            case FindNextRegex cmd    -> apply(Action.findNextRegex(cmd.str()));
            case FindPrevRegex cmd    -> apply(Action.findPrevRegex(cmd.str()));
            case FindAllRegex cmd     -> apply(Action.findAllRegex(cmd.str()));
            case Select cmd           -> apply(Action.select(cmd.str(), cmd.caseSensitive()));
            case SelectRegex cmd      -> apply(Action.selectRegex(cmd.str()));
            case GoTo cmd             -> apply(Action.goTo(cmd.rowNumber() - 1));
            case WrapLine cmd         -> model().apply(Action.wrapLine(cmd.width()));
            case ToggleLayout _       -> model().apply(Action.toggleLayout());
            case ToLowerCase _        -> model().apply(Action.replace(EditingFunctions.toLower, true));
            case ToUpperCase _        -> model().apply(Action.replace(EditingFunctions.toUpper, true));
            case IndentParen _        -> model().apply(Action.replace(EditingFunctions.toIndentParen, false));
            case IndentCurlyBrace _   -> model().apply(Action.replace(EditingFunctions.toIndentCurlyBrace, false));
            case Calc _               -> model().apply(Action.replace(EditingFunctions.toCalc, false));
            case Sort _               -> model().apply(Action.replace(EditingFunctions.sort, false));
            case Unique _             -> model().apply(Action.replace(EditingFunctions.unique, false));
            case DecToHex _           -> model().apply(Action.replace(EditingFunctions.decToHex, true));
            case DecToBin _           -> model().apply(Action.replace(EditingFunctions.decToBin, true));
            case HexToBin _           -> model().apply(Action.replace(EditingFunctions.hexToBin, true));
            case HexToDec _           -> model().apply(Action.replace(EditingFunctions.hexToDec, true));
            case BinToHex _           -> model().apply(Action.replace(EditingFunctions.binToHex, true));
            case BinToDec _           -> model().apply(Action.replace(EditingFunctions.binToDec, true));
            case Pwd _                -> inputText(() -> model().query(Query.contentPath).map(Path::getParent).orElse(null));
            case Pwf _                -> inputText(() -> model().query(Query.contentPath).orElse(null));
            case Now _                -> inputText(LocalDateTime::now);
            case Today _              -> inputText(LocalDate::now);
            case Forward _            -> forward();
            case Backward _           -> backward();
            case ZoomIn _             -> zoom( 1);
            case ZoomOut _            -> zoom(-1);
            case Help _               -> FxDialog.about(getScene().getWindow(), context).showAndWait();
            case Duplicate _          -> openRight(duplicate());
            case SearchInBrowser _    -> searchInBrowser(model().query(Query.selectedText));
            case TranslateInBrowser _ -> translateInBrowser(model().query(Query.selectedText));
            case Empty _              -> { }
        }
        paint();
    }

    private void apply(Action action) {
        if (model().query(Query.size) < BACKGROUND_THRESHOLD) {
            model().apply(action);
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                model().apply(action);
                return null;
            }
        };
        task.setOnSucceeded(_ -> paint());
        floatBar.handleProgress(task);
        var thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void inputText(Supplier<Object> supplier) {
        try {
            Object obj = supplier.get();
            if (obj == null) return;
            model().apply(Action.input(obj.toString()));
        } catch (Exception ignore) {
            log.log(System.Logger.Level.WARNING, "failed to input text", ignore);
        }
    }

    private void paint() {
        var model = model();
        model.paint(draw);
        Point p = model.query(Query.caretPoint);
        int selectedCounts = model.query(Query.selectedCounts);
        int foundCounts = model.query(Query.foundCounts);
        floatBar.setText(
            selectedCounts > 0 ? selectedCounts + " selected" : "",
            foundCounts > 0 ? foundCounts + " found" : "",
            p.row() + 1 + ":" + p.col(),
            HexFormat.of().formatHex(model.query(Query.bytesAtCaret)),
            model.query(Query.rowEndingSymbol),
            model.query(Query.charsetSymbol) + ((model.query(Query.bom).length > 0) ? "(BOM)" : ""));

        nameProperty.setValue(model.query(Query.modelName));
    }

    private void openWithChooser() {
        if (!canClose()) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Select file...");
        if (model().query(Query.contentPath).isPresent()) {
            fc.setInitialDirectory(
                model().query(Query.contentPath).get().toAbsolutePath().getParent().toFile());
        } else {
            fc.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
        }
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file == null) return;
        open(Session.of(file.toPath()));
    }

    void open(Path path) {
        if (path == null || !canClose()) return;
        if (Files.isReadableFile(path)) {
            open(Session.of(path));
        } else if (Files.isReadableDirectory(path)) {
            String list = String.join("\n", Files.listAbsolutePath(path));
            newEdit().inputText(() -> list.isEmpty() ? path.toAbsolutePath().toString() : list);
        } else {
            newEdit().inputText(path::toString);
        }
    }

    private void open(Session session) {

        boolean openInBackground = Files.size(session.path()) > BACKGROUND_THRESHOLD;

        // save previous session
        sessionHistory.push(model().getSession());

        close();
        model = openInBackground
            ? EditorModel.placeholderOf(session.path(), draw.fontMetrics(), scroll, context)
            : model.with(session);
        model.setSize(getWidth(), getHeight());
        nameProperty.setValue(model.query(Query.modelName));
        if (openInBackground) {
            Task<EditorModel> task = buildOpenTask(session);
            floatBar.handleProgress(task);
            var thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private Task<EditorModel> buildOpenTask(Session session) {
        final long size = Files.size(session.path());
        final long start = System.currentTimeMillis();
        AtomicLong workDone = new AtomicLong();
        Task<EditorModel> task = new Task<>() {
            @Override
            protected EditorModel call() {
                return EditorModel.of(session.path(),
                    draw.fontMetrics(), scroll, context,
                    n -> updateProgress(workDone.addAndGet(n), size));
            }
        };
        task.setOnSucceeded(_ -> {
            model = task.getValue();
            model.setSize(getWidth(), getHeight());
            log.log(System.Logger.Level.INFO, "opened %,d rows in %,d ms"
                .formatted(model.query(Query.rowSize), System.currentTimeMillis() - start));
        });
        return task;
    }

    @Override
    boolean needsCloseConfirmation() {
        return model().query(Query.modified) && query(Query.contentPath).isPresent();
    }

    @Override
    boolean canClose() {
        boolean canDiscard = true;
        if (model().query(Query.modified)) {
            var ret = FxDialog.confirmation(getScene().getWindow(),
                    "Are you sure you want to discard your changes?").showAndWait();
            canDiscard = ret.isPresent() && ret.get() == ButtonType.OK;
        }
        return canDiscard;
    }

    void forward() {
        sessionHistory.forward().ifPresent(session -> {
            if (canClose()) open(session);
        });
    }

    void backward() {
        sessionHistory.backward().ifPresent(session -> {
            if (canClose()) open(session);
        });
    }

    Optional<Session> stash() {
        final EditorModel m = model();
        return (m.query(Query.size) > 0)
            ? Optional.of(m.stash())
            : Optional.empty();
    }

    @Override
    Optional<Session> close() {
        EditorModel m = model();
        if (m == null) return Optional.empty();
        Optional<Session> restorableSession;
        if (m.query(Query.contentPath).isPresent()) {
            restorableSession = canClose()
                ? Optional.of(m.getSession())
                : Optional.empty();
        } else {
            restorableSession = (m.query(Query.size) > 0)
                ? Optional.of(m.stash())
                : Optional.empty();

        }
        m.close();
        return restorableSession;
    }

    private void save() {
        // TODO saving large files runs in the background
        if (model().query(Query.contentPath).isPresent()) {
            model().save(model().query(Query.contentPath).get());
        } else {
            saveAs();
        }
    }

    private void saveAs() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save As...");
        var current = model().query(Query.contentPath);
        fc.setInitialDirectory(current
            .map(value -> value.toAbsolutePath().getParent().toFile())
            .orElseGet(() -> Path.of(System.getProperty("user.home")).toFile()));
        File file = fc.showSaveDialog(getScene().getWindow());
        if (file == null) return;
        Path path = file.toPath();
        model().save(path);
        nameProperty.setValue(model().query(Query.modelName));
    }

    private EditorPane newEdit() {
        var editorPane = new EditorPane(context);
        tabContainer().add(editorPane);
        return editorPane;
    }

    private void reload() {
        model.reload();
    }

    private EditorPane newEditStage() {
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
        new CommandPalette(this, clazz, model().query(Query.selectedText))
            .showAndWait()
            .ifPresent(this::execute);
    }

    private InputMethodRequests inputMethodRequests() {
        return new InputMethodRequests() {
            @Override
            public Point2D getTextLocation(int i) {
                return model().imeLoc()
                        .map(loc -> canvas.localToScreen(loc.x(), loc.y()))
                        .orElse(null);
            }
            @Override
            public void cancelLatestCommittedText() {
                model().imeOff();
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

    @Override
    public ReadOnlyObjectProperty<Name> nameProperty() {
        return nameProperty;
    }

    @Override
    boolean externalChanged() {
        var contentPath = query(Query.contentPath);
        if (contentPath.isPresent()) {
            var current = Files.lastModifiedTime(contentPath.get());
            return current.compareTo(query(Query.lastModifiedTime).get()) != 0;
        }
        return false;
    }

    @Override
    void setCloseListener(Consumer<ContentPane> closeListener) {
        this.closeListener = closeListener;
    }

    <R> R query(Query<R> query) {
        return model().query(query);
    }

    SessionHistory sessionHistory() { return sessionHistory; }
    private EditorModel model() { return model; }

}
