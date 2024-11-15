package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Theme;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FloatBar extends HBox {

    private final Text text = new Text();

    public FloatBar(ScrollBar vScroll, ScrollBar hScroll) {

        setManaged(false);
        setBackground(new Background(new BackgroundFill(
            Color.web(Theme.dark.baseColor()),
            new CornerRadii(2),
            Insets.EMPTY)));

        text.setFont(Font.font("Consolas", 12));
        text.setFill(Color.web(Theme.dark.fgColor()));
        text.setText(" UTF-8 LF ");
        Bounds textBounds = text.getLayoutBounds();

        vScroll.layoutBoundsProperty().addListener((ob, o , n) -> {
            setLayoutY(n.getHeight() - textBounds.getHeight());
        });
        hScroll.layoutBoundsProperty().addListener((ob, o , n) -> {
            setLayoutX(n.getWidth() - vScroll.getWidth() - textBounds.getWidth());
        });
        hScroll.visibleProperty().addListener((ob, o , visible) -> {
            if (visible) {
                setLayoutY(vScroll.getHeight() - textBounds.getHeight() - hScroll.getHeight());
            } else {
                setLayoutY(vScroll.getHeight() - textBounds.getHeight());
            }
        });

        getChildren().add(new StackPane(text));
        setAlignment(Pos.BASELINE_CENTER);
        setPrefSize(textBounds.getWidth(), textBounds.getHeight());
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setWidth(textBounds.getWidth());
        setHeight(textBounds.getHeight());
    }

}
