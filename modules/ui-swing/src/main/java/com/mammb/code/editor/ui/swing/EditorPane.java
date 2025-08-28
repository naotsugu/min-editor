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
import com.mammb.code.editor.ui.Command;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * The editor pane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends JPanel {

    /** The context. */
    private final AppContext context;
    /** The draw. */
    private final SgDraw draw;
    /** The canvas. */
    private JComponent canvas;
    /** The editor model. */
    private EditorModel model;
    /** The screen scroll. */
    private final SgScreenScroll scroll = new SgScreenScroll(new JScrollBar(JScrollBar.VERTICAL), new JScrollBar(JScrollBar.HORIZONTAL));

    public EditorPane(AppContext ctx) {

        context = ctx;

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSize().height <= 0 || getSize().width <= 0) return;
                model().paint(draw.with(g));
            }
        };
        canvas.setOpaque(false);
        canvas.setFocusable(true);
        canvas.setBackground(Theme.current.baseColor()
            .as(c -> new Color(c[0], c[1], c[2], c[3])));
        canvas.setFont(new Font(context.config().fontName(), Font.PLAIN, (int) context.config().fontSize()));

        draw = new SgDraw(canvas);
        model = EditorModel.of(draw.fontMetrics(), scroll, context);

        setFocusable(true);
        setLayout(null);
        addFocusListener(focusListener());
        addKeyListener(keyListener());
        addMouseListener(mouseListener());
        addMouseMotionListener(mouseMotionListener());
        addMouseWheelListener(mouseWheelListener());
        addInputMethodListener(inputMethodListener());

        add(scroll.vScroll());
        add(scroll.hScroll());
        add(canvas);

        addComponentListener(componentListener());

        scroll.vScroll().addAdjustmentListener(e -> {
            model.scrollAt(e.getValue());
            canvas.repaint();
        });
        scroll.hScroll().addAdjustmentListener(e -> {
            model.scrollX(e.getValue());
            canvas.repaint();
        });
    }

    private ComponentListener componentListener() {
        return new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();

                canvas.setBounds(0, 0, width, height);
                model.setSize(width, height);

                var vScroll = scroll.vScroll();
                int vWidth = (vScroll.isVisible()) ? vScroll.getPreferredSize().width : 0;
                var hScroll = scroll.hScroll();
                int hHeight = (hScroll.isVisible()) ? hScroll.getPreferredSize().height : 0;

                vScroll.setBounds(width - vWidth, 0, vWidth, height - hHeight);
                hScroll.setBounds(0, height - hHeight, width - vWidth, hHeight);
            }
        };
    }

    private FocusListener focusListener() {
        return new FocusListener() {
            @Override public void focusGained(FocusEvent e) {
                model().setCaretVisible(true);
            }
            @Override public void focusLost(FocusEvent e) {
                model().setCaretVisible(false);
            }
        };
    }

    private KeyListener keyListener() {
        return new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {
                execute(CommandKeys.of(e));
            }
            @Override public void keyPressed(KeyEvent e) {
                execute(CommandKeys.of(e));
            }
            @Override public void keyReleased(KeyEvent e) {
            }
        };
    }

    private MouseListener mouseListener() {
        return new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {
            }
            @Override public void mousePressed(MouseEvent e) {
            }
            @Override public void mouseReleased(MouseEvent e) {
            }
            @Override public void mouseEntered(MouseEvent e) {
            }
            @Override public void mouseExited(MouseEvent e) {
            }
        };
    }

    private MouseMotionListener mouseMotionListener() {
        return new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent e) {
            }
            @Override public void mouseMoved(MouseEvent e) {
            }
        };
    }
    private MouseWheelListener mouseWheelListener() {
        return (MouseWheelEvent e) -> {
        };
    }

    private InputMethodListener inputMethodListener() {
        return new InputMethodListener() {
            @Override public void inputMethodTextChanged(InputMethodEvent event) {
            }
            @Override public void caretPositionChanged(InputMethodEvent event) {
            }
        };
    }

    void execute(Command command) {
        switch (command) {
            case Command.ActionCommand cmd -> model().apply(cmd.action());
            case Command.Empty _ -> { }
            default -> { }
        }
        repaint();
    }
    private EditorModel model() { return model; }
}
