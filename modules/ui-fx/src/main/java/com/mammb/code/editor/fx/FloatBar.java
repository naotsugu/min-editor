package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Objects;

public class FloatBar extends HBox {

    private final Text text = new Text();

    public FloatBar(ScrollBar vScroll, ScrollBar hScroll) {

        setManaged(false);
        setAlignment(Pos.BASELINE_CENTER);
        setBackground(new Background(new BackgroundFill(
            Color.web(Theme.dark.baseColor()),
            new CornerRadii(2),
            Insets.EMPTY)));

        text.setFont(Font.font("Consolas", 12));
        text.setFill(Color.web(Theme.dark.fgColor()));

        vScroll.layoutBoundsProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));
        hScroll.layoutBoundsProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));
        hScroll.visibleProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));
        layoutBoundsProperty().addListener((ob, o , n) -> layout(vScroll, hScroll));

        getChildren().add(text);
        layout(vScroll, hScroll);
    }

    public void setText(String... strings) {
        String joined = String.join(" ", strings);
        if (Objects.equals(text.getText(), joined)) {
            return;
        }
        text.setText(String.join(" ", strings));
        setWidth(text.getLayoutBounds().getWidth() + 16);
        setHeight(text.getLayoutBounds().getHeight());
    }

    private void layout(ScrollBar vScroll, ScrollBar hScroll) {
        setLayoutX(hScroll.getWidth() - vScroll.getWidth() - getWidth());
        setLayoutY(vScroll.getHeight() - getHeight() - (hScroll.isVisible() ? hScroll.getHeight() : 0));
    }

}
