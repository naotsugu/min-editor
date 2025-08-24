/*
 * Copyright 2023-2025 the original author or authors.
 * <p>
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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.Theme;
import javax.swing.*;
import java.awt.*;

/**
 * The editor pane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends JPanel {

    /** The context. */
    private final AppContext context;
    /** The canvas. */
    private final Canvas canvas;
    /** The draw. */
    private final SgDraw draw;
    /** The editor model. */
    private EditorModel model;
    /** The screen scroll. */
    private final SgScreenScroll scroll = new SgScreenScroll();


    public EditorPane(AppContext ctx) {
        context = ctx;

        canvas = new Canvas() {
            @Override public void paint(Graphics g) {
                super.paint(g);
                model.paint(draw.with(g));
            }
        };
        add(canvas);
        canvas.setFocusable(false);
        canvas.setBackground(Theme.current.baseColor()
            .as(c -> new Color(c[0], c[1], c[2], c[3])));
        Font font = new Font(context.config().fontName(), Font.PLAIN, (int) context.config().fontSize());
        canvas.setFont(font);
        System.out.println(canvas.getFontMetrics(font));
        draw = new SgDraw(canvas);
        model = EditorModel.of(draw.fontMetrics(), scroll, context);

    }

}
