package com.mammb.code.editor2.ui.pane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class UiCaret extends Path {

    /** The timeline. */
    private final Timeline timeline = new Timeline();

    /**
     * Constructor.
     */
    public UiCaret() {

        setStrokeWidth(2);
        setStroke(Color.ORANGE);
        setManaged(false);

        timeline.setCycleCount(-1);
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(500),
                e -> setVisible(!isVisible())));
        timeline.play();

        //setShape(defaultShape());
    }

}
