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

public class StatusBar extends HBox {

    public static final double HEIGHT = 15;
    public static final double WIDTH = 150;

    /** The Context. */
    private final Context context;

    private final Text lineEndingText;
    private final Text charsetText;
    private final Text caretPointText;

    public StatusBar(Context context) {

        this.context = context;

        setBackground(new Background(new BackgroundFill(
            Color.web(context.preference().bgColor()).deriveColor(0, 1, 1, 0.8),
            new CornerRadii(3, 0, 0, 0, false),
            Insets.EMPTY)));
        setCursor(Cursor.TEXT);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setPrefSize(WIDTH, HEIGHT);
        setSpacing(HEIGHT);
        setPadding(new Insets(0, HEIGHT / 3, 0, HEIGHT / 3));
        setAlignment(Pos.CENTER_RIGHT);
        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT);

        this.caretPointText = createText();
        this.lineEndingText = createText();
        this.charsetText    = createText();
    }

    public void bind(StateHandler stateHandler) {
        stateHandler.addLineEndingChanged(c ->
            lineEndingText.setText(c.toString()));
        stateHandler.addCharsetChanged(c ->
            charsetText.setText(c.toString()));
        stateHandler.addCaretPointChanged(c ->
            caretPointText.setText(String.valueOf(c.cpOffset())));
    }

    private Text createText() {
        var text = new Text();
        text.setFill(Color.web(context.preference().fgColor()));
        text.setFont(new Font(HEIGHT * 0.75));
        var pane = new StackPane(text);
        getChildren().add(pane);
        return text;
    }

}
