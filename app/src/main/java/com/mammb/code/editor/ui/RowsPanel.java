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
package com.mammb.code.editor.ui;

import com.mammb.code.editor.ui.util.Colors;
import com.mammb.code.editor.ui.util.TextMetrics;
import com.mammb.code.editor.ui.util.Texts;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * RowsPanel.
 * @author Naotsugu Kobayashi
 */
public class RowsPanel extends Pane {

    /** The right margin. */
    private static final double marginRight = 8;

    /** The text flow. */
    private final TextFlow flow;

    /** The number of origin row index. */
    private int origin = 0;


    /**
     * Constructor.
     * @param flow the text flow
     */
    public RowsPanel(TextFlow flow, ScreenBound screenBound) {
        this.flow = flow;

        // the panel scrolls in sync with the text flow.
        translateYProperty().bind(flow.translateYProperty());
        // The starting row number is sync with screen bound row offset.
        screenBound.rowOffsetProperty().addListener((os, ov, nv) -> draw(nv.intValue()));
    }


    /**
     * Draw the row number.
     * @param origin the origin number(zero based)
     */
    private void draw(int origin) {
        this.origin = origin;
        getChildren().clear();
        double y = 0;
        int n = -1;
        for (TextMetrics.Line line : flow.metrics().lines()) {
            if (n != line.rowIndex()) {
                n = line.rowIndex();
                getChildren().add(asText(origin + n + 1, y));
            }
            y += line.height();
        }
    }


    /**
     * Redraw the row number.
     */
    public void redraw() {
        draw(origin);
    }


    /**
     * Create a row number text.
     * @param n the number of row
     * @param y the location of y
     * @return a row number text
     */
    private Text asText(int n, double y) {
        Text text = Texts.asText(Integer.toString(n), Colors.foregroundModest);
        text.setTextOrigin(VPos.TOP);
        text.setTextAlignment(TextAlignment.RIGHT);
        double x = getWidth() - text.getLayoutBounds().getWidth() - marginRight;
        if (x < 0) {
            x = getParent().getLayoutBounds().getWidth()
                - text.getLayoutBounds().getWidth()
                - marginRight;
        }
        text.setX(x);
        text.setY(y);
        return text;
    }

}
