/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.core.ScreenScroll;
import javax.swing.*;

/**
 * The screen scroll.
 * @author Naotsugu Kobayashi
 */
public class SgScreenScroll implements ScreenScroll {

    /** The vertical scrollbar. */
    private final JScrollBar vScroll;

    /** The horizontal scroll bar. */
    private final JScrollBar hScroll;

    /**
     * Constructs an SgScreenScroll instance with the specified vertical and horizontal scrollbars.
     * @param vScroll the vertical scrollbar to be managed by this instance
     * @param hScroll the horizontal scrollbar to be managed by this instance
     */
    public SgScreenScroll(JScrollBar vScroll, JScrollBar hScroll) {
        this.vScroll = vScroll;
        this.hScroll = hScroll;
    }

    @Override
    public void vertical(int min, int max, int val, int len) {
        vScroll.setMinimum(min);
        vScroll.setMaximum(max);
        vScroll.setValue(val);
        vScroll.setVisibleAmount(len);
        //vScroll.setVisible(max > len);
    }

    @Override
    public void horizontal(double min, double max, double val, double len) {
        hScroll.setMinimum((int) min);
        hScroll.setMaximum((int) max);
        hScroll.setValue((int) val);
        hScroll.setVisibleAmount((int) len);
        //hScroll.setVisible(max > len);
    }

    @Override
    public double xVal() {
        return hScroll.getValue();
    }

    @Override
    public int yVal() {
        return vScroll.getValue();
    }

    public JScrollBar vScroll() {
        return vScroll;
    }

    public JScrollBar hScroll() {
        return hScroll;
    }

}
