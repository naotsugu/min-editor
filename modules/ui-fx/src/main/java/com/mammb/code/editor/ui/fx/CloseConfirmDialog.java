/*
 * Copyright 2023-2026 the original author or authors.
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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.util.Objects;

/**
 * The CloseConfirmDialog.
 * @author Naotsugu Kobayashi
 */
public class CloseConfirmDialog extends HBox {

    /** The dialog answer options. */
    public enum Answer { SAVE, DISCARD, CANCEL }

    private final Stage dialog = new Stage();
    private final Scene scene;

    private String targetName;
    private Label messageLabel;
    private Button saveButton;
    private Button discardButton;
    private Button cancelButton;

    private Runnable onSave;
    private Runnable onDiscard;
    private Runnable onCancel;

    private Answer answer = Answer.CANCEL;

    /**
     * Constructor.
     * @param owner the owner
     */
    public CloseConfirmDialog(Window owner) {
        this(owner, "");
    }

    /**
     * Constructor.
     * @param owner the owner
     * @param targetName the target file/buffer name
     */
    public CloseConfirmDialog(Window owner, String targetName) {
        Objects.requireNonNull(owner);
        this.targetName = targetName;
        getStyleClass().add("close-confirm-dialog");
        setStyle("""
            -fx-border-style: solid;
            -fx-border-color: derive(-fx-base, 30%);;
            -fx-border-width: 1;
            -fx-background-radius: 12px;
            -fx-border-radius: 12px;
            """);

        dialog.initOwner(owner);
        dialog.setTitle("Confirmation");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setResizable(false);
        dialog.addEventHandler(KeyEvent.ANY, keyEventListener);

        scene = new Scene(this, Color.TRANSPARENT);
        final Scene ownerScene = owner.getScene();
        if (ownerScene != null) {
            if (ownerScene.getUserAgentStylesheet() != null) {
                scene.setUserAgentStylesheet(ownerScene.getUserAgentStylesheet());
            }
            scene.getStylesheets().addAll(ownerScene.getStylesheets());
        }

        buildUI();
        updateMessage();

        dialog.setScene(scene);
    }

    private void buildUI() {
        setPadding(new Insets(20, 24, 20, 24));
        setSpacing(16);
        setAlignment(Pos.CENTER_LEFT);

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(e -> {
            answer = Answer.SAVE;
            if (onSave != null) onSave.run();
            closeDialog();
        });

        discardButton = new Button("Discard");
        discardButton.setOnAction(e -> {
            answer = Answer.DISCARD;
            if (onDiscard != null) onDiscard.run();
            closeDialog();
        });

        cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> {
            answer = Answer.CANCEL;
            if (onCancel != null) onCancel.run();
            closeDialog();
        });

        HBox buttonBox = new HBox(8, saveButton, discardButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox contentBox = new VBox(20, messageLabel, buttonBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        getChildren().setAll(contentBox);
    }

    private void updateMessage() {
        if (messageLabel != null) {
            if (targetName != null && !targetName.isBlank()) {
                messageLabel.setText("Do you want to save the changes you made to [" + targetName + "] ?");
            } else {
                messageLabel.setText("Do you want to save the changes?");
            }
        }
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
        updateMessage();
    }

    public void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public void setOnDiscard(Runnable onDiscard) {
        this.onDiscard = onDiscard;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public Answer showAndWait() {
        answer = Answer.CANCEL;
        if (dialog.getScene() == null) {
            dialog.setScene(scene);
        }
        if (dialog.getOwner() != null) {
            dialog.widthProperty().addListener(positionAdjuster);
            dialog.heightProperty().addListener(positionAdjuster);
            positionAdjuster.invalidated(null);
        }
        dialog.showAndWait();
        return answer;
    }

    public void show() {
        answer = Answer.CANCEL;
        if (dialog.getScene() == null) {
            dialog.setScene(scene);
        }
        if (dialog.getOwner() != null) {
            dialog.widthProperty().addListener(positionAdjuster);
            dialog.heightProperty().addListener(positionAdjuster);
            positionAdjuster.invalidated(null);
        }
        dialog.show();
    }

    private final InvalidationListener positionAdjuster = new InvalidationListener() {
        @Override
        public void invalidated(Observable ignored) {
            if (Double.isNaN(dialog.getWidth()) || Double.isNaN(dialog.getHeight())
                || dialog.getWidth() <= 0 || dialog.getHeight() <= 0) {
                return;
            }
            dialog.widthProperty().removeListener(positionAdjuster);
            dialog.heightProperty().removeListener(positionAdjuster);
            fixPosition();
        }
    };

    private void fixPosition() {
        Window w = dialog.getOwner();
        if (w != null) {
            double wx = w.getX() + (w.getWidth() / 2);
            double wy = w.getY() + (w.getHeight() / 2);
            double x = wx - (dialog.getWidth() / 2);
            double y = wy - (dialog.getHeight() / 2);
            dialog.setX(x);
            dialog.setY(y);
        }
    }

    private void closeDialog() {
        dialog.setScene(null);
        dialog.close();
    }

    private final EventHandler<KeyEvent> keyEventListener = e -> {
        switch (e.getCode()) {
            case ESCAPE -> {
                answer = Answer.CANCEL;
                if (onCancel != null) onCancel.run();
                closeDialog();
            }
            default -> { }
        }
    };

}
