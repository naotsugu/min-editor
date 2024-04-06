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
package com.mammb.code.editor.ui.app;

import com.mammb.code.editor.ui.app.control.UiToast;
import com.mammb.code.editor.ui.pane.EditorDownCall;
import com.mammb.code.editor.ui.pane.EditorPane;
import com.mammb.code.editor.ui.pane.Session;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The EditorStage.
 * @author Naotsugu Kobayashi
 */
public class EditorStage {

    /** The application. */
    private final App app;
    /** The stage. */
    private final Stage stage;
    /** The context. */
    private final Context context;

    /** The up call. */
    private final AppEditorUpCall upCall;
    /** The down call. */
    private final EditorDownCall downCall;
    /** The editor pane. */
    private final EditorPane editorPane;
    /** The command bar. */
    private final CommandBar bar;
    /** The editor session. */
    private final EditorSession session;

    /** This timeline is used to detect the long presses of keys. */
    private Timeline timeline;


    /**
     * Constructor.
     * @param stage the stage
     * @param context the context
     * @param app the application
     */
    public EditorStage(Stage stage, Context context, App app) {

        this.stage = stage;
        this.context = context;
        this.app = app;

        this.upCall = new AppEditorUpCall();
        this.editorPane = new EditorPane(context, upCall);
        this.downCall = editorPane.downCall();
        this.bar = new CommandBar();
        this.session = new EditorSession();

        initStage();
        initUpCall();
        initBar();
        initSession();

        editorPane.setOnContextMenuRequested(e -> {
            var cm = new EditorContextMenu(downCall);
            cm.show(stage, e.getScreenX(), e.getScreenY());
        });
    }


    /**
     * Initialize the stage.
     */
    private void initStage() {
        stage.getIcons().add(new Image(
            Objects.requireNonNull(App.class.getResourceAsStream("/icon.png"))));
        stage.setTitle(Version.appName);
        stage.setOnCloseRequest(editorPane::handleCloseRequest);
    }


    /**
     * Initialize the up call.
     */
    private void initUpCall() {
        upCall.addPathChangedListener(c -> {
            bar.setPathText(c.session().path());
            session.push(c.session(), c.prevSession());
            stage.setTitle(String.join(" - ", Version.appName, c.session().path().getFileName().toString()));
        });
        upCall.addContentModifiedListener(c -> bar.setPathModified(c.modified()));
    }


    /**
     * Initialize the command bar.
     */
    private void initBar() {
        bar.setOnPathTextCommitted((text, ke) -> downCall.requestPathChange(Session.of(Path.of(text))));
        bar.setOnPathSelected(p -> downCall.requestPathChange(Session.of(p)));
        bar.setOnForwardClicked(() -> downCall.requestPathChange(session.forward()));
        bar.setOnBackwardClicked(() -> downCall.requestPathChange(session.backward()));
        bar.setOnSearchTextCommitted((text, ke) -> {
            downCall.requestFind(text, !ke.isShortcutDown());
            bar.setVisibleSearchField(null);
        });
        bar.setOnExit(() -> Platform.runLater(downCall::requestFocus));
    }


    /**
     * Initialize the session.
     */
    private void initSession() {
        session.setForwardDisableProperty(bar.forwardDisableProperty());
        session.setBackwardDisableProperty(bar.backwardDisableProperty());
    }


    /**
     * The key pressed handler.
     */
    void handleKeyPressed(KeyEvent e) {

        stopTimeline();

        if (AppKeys.SC_N.match(e)) {
            e.consume();
            Stage newStage = new Stage();
            newStage.setX(stage.getX() + 15);
            newStage.setY(stage.getY() + 15);
            app.buildScene(newStage, context);
            newStage.show();
            newStage.requestFocus();

        } else if (AppKeys.SC_FORWARD.match(e)) {
            downCall.requestPathChange(session.forward());

        } else if (AppKeys.SC_BACKWARD.match(e)) {
            downCall.requestPathChange(session.backward());

        } else if (AppKeys.SC_F.match(e)) {
            var query = EditorDownCall.selectedText();
            downCall.requestQuery(query);
            if (query.ret() != null && !query.ret().isBlank() && !query.ret().contains("\n")) {
                bar.setVisibleSearchField(query.ret());
                downCall.requestSelectClear();
            }
        }

        if (e.isShortcutDown() && e.getText().isBlank()) {
            //var query = EditorDownCall.selectedText();
            //downCall.requestQuery(query);
            //if (query.ret() != null && !query.ret().isBlank()) {
                startTimeline();
            //}
        }
    }


    /**
     * The key released handler.
     */
    void handleKeyReleased(KeyEvent e) {
        stopTimeline();
    }

    /**
     * Get the editor pane.
     * @return the editor pane
     */
    EditorPane editorPane() {
        return editorPane;
    }


    /**
     * Get the command bar.
     * @return the command bar
     */
    CommandBar commandBar() {
        return bar;
    }


    /**
     * Bind the scene to this stage.
     * @param scene the scene
     * @return this stage
     */
    Stage bind(Scene scene) {
        stage.setScene(scene);
        return stage;
    }


    /**
     * Start the timeline.
     */
    private void startTimeline() {
        timeline = new Timeline();
        final KeyFrame kf = new KeyFrame(Duration.millis(1000), e -> {
            toast = UiToast.of(stage, STR."""
                Ctrl + U : Toggle upper case
                Ctrl + . : Repeat
                Ctrl + 7 : Unique
                Ctrl + 8 : Sort
                Ctrl + 9 : Calculate
                Ctrl + 0 : Convert to hex""");
            stage.requestFocus();
        });
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
    UiToast toast = null;

    /**
     * Stop the timeline.
     */
    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        if (toast != null) {
            toast.close();
        }
    }

}
