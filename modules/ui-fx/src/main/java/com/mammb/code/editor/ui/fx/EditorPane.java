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
package com.mammb.code.editor.ui.fx;

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
import com.mammb.code.editor.platform.AppVersion;
import com.mammb.code.editor.ui.base.Command;
import com.mammb.code.editor.ui.base.DrawImpl;
import com.mammb.code.editor.ui.base.Command.*;
import com.mammb.code.editor.ui.base.LruList;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.BoundingBox;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
    private final FxAppContext context;
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
    /** The find command history. */
    private final LruList<FindCommand> findCommandHistory = new LruList<>(15);
    /** The file path property. */
    private final SimpleObjectProperty<Name> nameProperty = new SimpleObjectProperty<>(Name.EMPTY);

    /** The close listener. */
    private Consumer<ContentPane> closeListener;

    /**
     * Constructor.
     * @param ctx the application context
     */
    public EditorPane(FxAppContext ctx) {

        context = ctx;
        Font font = Font.font(context.config().fontName(), context.config().fontSize());

        canvas = new Canvas();
        canvas.setManaged(false);
        canvas.setFocusTraversable(true);
        canvas.setOnMouseMoved(this::handleMouseMoved);

        draw = new DrawImpl(new FxGraphicsDraw(canvas.getGraphicsContext2D(), font));
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
        // After giving it some thought, I don't see any necessity to hide the caret.
        // canvas.focusedProperty().addListener((_, _, n) -> {
        //     model().setCaretVisible(n);
        //     paint(); // TODO only caret draw
        // });
        nameProperty.setValue(model.query(Query.modelName));
    }

    public EditorPane bindLater(Session session) {
        Platform.runLater(() -> {
            if (session.hasPath()) {
                open(session);
            } else if (session.hasAltPath()) {
                model = model.with(session);
                model.setSize(getWidth(), getHeight());
            }
            paint();
        });
        return this;
    }

    @Override
    public void focus() {
        canvas.requestFocus();
        paint();
    }

    private void handleContextMenuRequested(ContextMenuEvent e) {
        new AppContextMenu(this)
            .show(getScene().getWindow(), e.getScreenX(), e.getScreenY());
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

    private EditorPane diff(String pathString, boolean withoutFold) {
        Path path = (pathString == null || pathString.isBlank()) ? null : Path.of(pathString);
        return new EditorPane(context)
            .with(model().getSession(Session.diff(path, withoutFold)));
    }

    private EditorPane duplicate() {
        return session().map(session -> new EditorPane(context)
            .with(session.asReadonly())).orElse(null);
    }

    private EditorPane binary() {
        return new EditorPane(context)
            .with(model().getSession(Session.binary(model().stash().altPath())));
    }

    private EditorPane foundFilter(int contextSize) {
        var editorPane = new EditorPane(context)
            .with(model().getSession(Session.rowFilter(model().query(Query.foundRows), contextSize)));
        var lastCmd = findCommandHistory.peek();
        if (lastCmd != null) {
            Platform.runLater(() -> editorPane.execute(lastCmd));
        }
        return editorPane;
    }

    private void openInFiler(Path path) {
        if (path != null && Files.isReadableDirectory(path.getParent())) {
            context.getApp().getHostServices().showDocument(path.getParent().toUri().toString());
        }
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

    private void colorPick() {
        var d = new FxColorDialog(getScene().getWindow());
        try {
            d.setColor(Color.valueOf(model().query(Query.selectedText)));
        } catch (Exception ignore) { }
        d.setOnSelect(() -> execute(CommandKeys.of(Action.input(d.getSelectedWebColor()))));
        d.show();
    }

    private void openRight(ContentPane contentPane) {
        var container = tabContainer();
        if (container != null && contentPane != null) {
            container.parent().addRight(contentPane);
        }
    }

    private void openRightWithFocus(ContentPane contentPane) {
        var container = tabContainer();
        if (container != null && contentPane != null) {
            var pane = container.parent().addRight(contentPane);
            Platform.runLater(pane::focus);
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
        if (e.isConsumed()) return;
        execute(CommandKeys.of(e));
    }

    private void handleDragDetect(MouseEvent e) {
    }

    private void handleDragOver(DragEvent e) {
        if (!new BoundingBox(20, 20, getWidth() - 40, getHeight() - 40).contains(e.getX(), e.getY())) {
            return; // ignore drops to the edge
        }
        if (e.getDragboard().hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
    }

    private void handleDragDropped(DragEvent e) {
        if (!new BoundingBox(20, 20, getWidth() - 40, getHeight() - 40).contains(e.getX(), e.getY())) {
            return; // ignore drops to the edge
        }
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
            case SaveWithLF _         -> saveWith(null, "LF");
            case SaveWithCRLF _       -> saveWith(null, "CRLF");
            case SaveWith cmd         -> saveWith(cmd.charset(), null);
            case New _                -> newEdit();
            case ReloadWith cmd       -> reload(cmd.charset());
            case TabClose _           -> { if (closeListener != null) closeListener.accept(this); }
            case Palette cmd          -> showCommandPalette(cmd.initial());
            case Open cmd             -> open(Path.of(cmd.path()));
            case OpenRecent _         -> openRecent();
            case Config _             -> newEdit().open(Session.of(context.config().path()));
            case FindNext cmd         -> apply(Action.findNext(cmd.str(), cmd.caseInsensitive()));
            case FindPrev cmd         -> apply(Action.findPrev(cmd.str(), cmd.caseInsensitive()));
            case FindAll cmd          -> apply(Action.findAll(cmd.str(), cmd.caseInsensitive()));
            case FindNextRegex cmd    -> apply(Action.findNextRegex(cmd.str()));
            case FindPrevRegex cmd    -> apply(Action.findPrevRegex(cmd.str()));
            case FindAllRegex cmd     -> apply(Action.findAllRegex(cmd.str()));
            case Select cmd           -> apply(Action.select(cmd.str(), cmd.caseInsensitive()));
            case SelectRegex cmd      -> apply(Action.selectRegex(cmd.str()));
            case GoTo cmd             -> apply(Action.goTo(cmd.rowNumber() - 1));
            case WrapLine cmd         -> model().apply(Action.wrapLine(cmd.width()));
            case SetTabStop cmd       -> model().apply(Action.setTabStop(cmd.size()));
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
            case ToggleCodepoint _    -> model().apply(Action.replace(EditingFunctions.toggleCodepoint, true));
            case NormalizeAscii _     -> model().apply(Action.replace(EditingFunctions.normalizeAscii, true));
            case AutoFill _           -> model().apply(Action.replace(EditingFunctions.autoFill(model().query(Query.textAtCarets)), true));
            case Pwd _                -> inputText(() -> model().query(Query.contentPath).map(Path::getParent).orElse(null));
            case Pwf _                -> inputText(() -> model().query(Query.contentPath).orElse(null));
            case Now _                -> inputText(LocalDateTime::now);
            case Today _              -> inputText(LocalDate::now);
            case Forward _            -> forward();
            case Backward _           -> backward();
            case ZoomIn _             -> zoom( 1);
            case ZoomOut _            -> zoom(-1);
            case ColorPick _          -> colorPick();
            case Help _               -> FxDialog.about(getScene().getWindow(), context).showAndWait();
            case Diff _               -> openRight(diff(null, false));
            case DiffFoldOff _        -> openRight(diff(null, true));
            case DiffWith cmd         -> openRight(diff(cmd.path(), false));
            case Duplicate _          -> openRightWithFocus(duplicate());
            case BinaryView _         -> openRight(binary());
            case FoundFilterView cmd  -> openRightWithFocus(foundFilter(cmd.contextSize()));
            case OpenInFiler _        -> openInFiler(model().query(Query.contentPath).orElse(null));
            case SearchInBrowser _    -> searchInBrowser(model().query(Query.selectedText));
            case TranslateInBrowser _ -> translateInBrowser(model().query(Query.selectedText));
            case Empty _              -> { }
        }
        if (command instanceof FindCommand cmd) {
            findCommandHistory.push(cmd);
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
        } catch (Exception e) {
            log.log(System.Logger.Level.WARNING, "failed to input text", e);
        }
    }

    private void paint() {
        var model = model();
        model.paint(draw);
        Point p = model.query(Query.caretPoint);
        int selectedCounts = model.query(Query.selectedCounts);
        int foundRowCounts = model.query(Query.foundRowCounts);
        int foundCounts = (foundRowCounts > 0) ? model.query(Query.foundCounts) : 0;
        floatBar.setText(
            selectedCounts > 0 ? String.format("%,d selected", selectedCounts): "",
            foundRowCounts > 0 ? String.format("%,d found(%,d rows)", foundCounts, foundRowCounts) : "",
            String.format("%,d", p.row() + 1) + ":" + String.format("%,d", p.col()),
            HexFormat.of().formatHex(model.query(Query.bytesAtCaret)),
            model.query(Query.rowEndingSymbol),
            model.query(Query.charCodeSymbol) + ((model.query(Query.bom).length > 0) ? "(BOM)" : ""));

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

    void openRecent() {
        var window = getScene().getWindow();
        SelectOneMenu.of(context.recents(), path -> {
            if (path == null || !canClose()) return;
            open(Session.of(path));
            paint();
        }).show(window, window.getX(), window.getY() + 55);
    }

    private void open(Session session) {

        boolean openInBackground = Files.size(session.path()) > BACKGROUND_THRESHOLD;

        // save previous session
        sessionHistory.push(model().getSession());

        close(false);
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
            nameProperty.setValue(model.query(Query.modelName));
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
                    "Are you sure you want to discard your changes?\n" +
                    model().query(Query.modelName).plain() + " has been modified.")
                .showAndWait();
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

    Optional<Session> session() {
        var session = model().getSession();
        return session.isEmpty() ? Optional.empty() : Optional.of(session);
    }

    @Override
    Optional<Session> close(boolean force) {
        EditorModel m = model();
        if (m == null) return Optional.empty();
        Optional<Session> restorableSession;
        var contentPath = m.query(Query.contentPath);
        if (contentPath.isPresent()) {
            context.closed(contentPath.get());
            restorableSession = force || canClose()
                ? Optional.of(m.getSession())
                : Optional.empty();
        } else {
            var session = m.stash();
            restorableSession = session.isEmpty() ? Optional.empty() : Optional.of(session);
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

    private void saveWith(Charset charset, String endingSymbol) {
        if (model().query(Query.contentPath).isEmpty()) {
            saveAs();
        }
        model().saveWith(charset, endingSymbol);
    }

    private EditorPane newEdit() {
        var editorPane = new EditorPane(context);
        var tabContainer = tabContainer();
        if (tabContainer != null) tabContainer.add(editorPane);
        return editorPane;
    }

    private void reload(Charset charset) {
        model.reload(charset);
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
        stage.setTitle(AppVersion.appName);
        stage.show();
        return editorPane;
    }

    private void showCommandPalette(Class<? extends Command> clazz) {
        new CommandPalette(this, clazz, model())
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
            if (current != null) {
                return query(Query.lastModifiedTime)
                    .map(m -> m.compareTo(current) != 0).orElse(false);
            }
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

    private EditorPane with(Session session) {
        model = model.with(session);
        return this;
    }

}
