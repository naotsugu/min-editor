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

import com.mammb.code.editor.ui.model.StateHandler;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * StatusBar.
 * @author Naotsugu Kobayashi
 */
public class StatusBar extends HBox {

    public static final double HEIGHT = 14;

    /** The Context. */
    private final Context context;

    /** The line ending text. */
    private final Text lineEndingText;
    /** The charset text. */
    private final Text charsetText;
    /** The caret text. */
    private final Text caretText;

    /** The caret text string. */
    private String caretPointString = "";
    /** The selection text string. */
    private String selectionString = "";


    /**
     * Constructor.
     * @param context the Context
     */
    public StatusBar(Context context) {

        this.context = context;
        this.caretText = createText();
        this.lineEndingText = createText();
        this.charsetText    = createText();

        setBackground(new Background(new BackgroundFill(
            Color.web(context.preference().bgColor()),
            new CornerRadii(3, 0, 0, 0, false),
            Insets.EMPTY)));
        setCursor(Cursor.TEXT);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setPrefSize(0, HEIGHT);
        setSpacing(HEIGHT);
        setPadding(new Insets(0, HEIGHT / 3, 0, HEIGHT / 3));
        setAlignment(Pos.CENTER_RIGHT);
        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT);

    }


    /**
     * Bind state handler.
     * @param stateHandler the state handler
     */
    public void bind(StateHandler stateHandler) {
        stateHandler.addLineEndingChanged(c -> lineEndingText.setText(c.asString()));
        stateHandler.addCharsetChanged(c -> charsetText.setText(c.toString()));
        stateHandler.addCaretPointChanged(c -> caretText.setText(selectionString + " " + (caretPointString = c.asString())));
        stateHandler.addSelectionChanged(c -> caretText.setText((selectionString = c.asString()) + " " + caretPointString));
    }


    /**
     * Create text.
     * @return the text
     */
    private Text createText() {
        var text = new Text();
        text.setFill(Color.web(context.preference().fgColor()).deriveColor(0, 1, 1, 0.9));
        text.setFont(new Font(HEIGHT * 0.75));
        getChildren().add(new StackPane(text));
        return text;
    }

}
