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

import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * The editor pane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends JPanel {

    /** The context. */
    private final AppContext context;
    /** The draw. */
    private final SgDraw draw;
    /** The editor model. */
    private EditorModel model;
    /** The screen scroll. */
    private final SgScreenScroll scroll = new SgScreenScroll();

    public EditorPane(AppContext ctx) {
        context = ctx;
        setBackground(Theme.current.baseColor()
            .as(c -> new Color(c[0], c[1], c[2], c[3])));
        Font font = new Font(context.config().fontName(), Font.PLAIN, (int) context.config().fontSize());
        setFont(font);
        draw = new SgDraw(this);
        model = EditorModel.of(draw.fontMetrics(), scroll, context);
        addComponentListener(componentListener());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getSize().height <= 0 || getSize().width <= 0) return;
        model.paint(draw.with(g));
    }

    private ComponentListener componentListener() {
        return new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                model.setSize(size.width, size.height);
                repaint();
            }
            @Override
            public void componentMoved(ComponentEvent e) {
            }
            @Override
            public void componentShown(ComponentEvent e) {
            }
            @Override
            public void componentHidden(ComponentEvent e) {
            }
        };
    }

}
