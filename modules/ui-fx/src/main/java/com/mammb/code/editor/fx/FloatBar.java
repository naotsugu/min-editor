package com.mammb.code.editor.fx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class FloatBar extends HBox {

    public final DoubleProperty height = new SimpleDoubleProperty(14);
    public final DoubleProperty width = new SimpleDoubleProperty(50);

    public FloatBar(ScrollBar vScroll, ScrollBar hScroll) {
        setManaged(false);
        vScroll.layoutBoundsProperty().addListener((ob, o , n) -> {
            setLayoutY(n.getHeight() - height.get());
        });
        hScroll.layoutBoundsProperty().addListener((ob, o , n) -> {
            setLayoutX(n.getWidth() - width.get());
        });
        hScroll.visibleProperty().addListener((ob, o , visible) -> {
            if (visible) {
                setLayoutY(vScroll.getHeight() - height.get() - hScroll.getHeight());
            } else {
                setLayoutY(vScroll.getHeight() - height.get());
            }
        });
        setAlignment(Pos.BASELINE_CENTER);
        setPrefSize(width.get(), height.get());
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

}
