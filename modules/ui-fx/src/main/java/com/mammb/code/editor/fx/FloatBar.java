package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class FloatBar extends HBox {

    private final Label label = new Label();

    public FloatBar(ScrollBar vScroll, ScrollBar hScroll) {

        setManaged(false);
        setBackground(new Background(new BackgroundFill(
            Color.web(Theme.dark.baseColor()),
            new CornerRadii(2),
            Insets.EMPTY)));

        label.setText("UTF-8 LF");
        label.setStyle("""
            -fx-font: 12px "Consolas";
            """);

        vScroll.layoutBoundsProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));
        hScroll.layoutBoundsProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));
        hScroll.visibleProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));
        layoutBoundsProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));

        getChildren().add(label);
        layout(vScroll, hScroll);
        setAlignment(Pos.BASELINE_CENTER);
        setPrefSize(100, 15);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setWidth(100);
        setHeight(15);
    }

    private void layout(ScrollBar vScroll, ScrollBar hScroll) {
        setLayoutX(hScroll.getWidth() - vScroll.getWidth() - getWidth());
        if (hScroll.isVisible()) {
            setLayoutY(vScroll.getHeight() - getHeight() - hScroll.getHeight());
        } else {
            setLayoutY(vScroll.getHeight() - getHeight());
        }
    }

}
