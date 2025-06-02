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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.ScreenScroll;
import javafx.scene.control.ScrollBar;

/**
 * A concrete implementation of the ScreenScroll interface for managing
 * vertical and horizontal scrollbars using JavaFX ScrollBar components.
 * This class provides methods to set scroll values and retrieve the
 * horizontal scroll position.
 * @author Naotsugu Kobayashi
 */
public class FxScreenScroll implements ScreenScroll {

    /** The vertical scrollbar. */
    private final ScrollBar vScroll;

    /** The horizontal scroll bar. */
    private final ScrollBar hScroll;

    /**
     * Constructs an FxScreenScroll instance with the specified vertical and horizontal scrollbars.
     * @param vScroll the vertical scrollbar to be managed by this instance
     * @param hScroll the horizontal scrollbar to be managed by this instance
     */
    public FxScreenScroll(ScrollBar vScroll, ScrollBar hScroll) {
        this.vScroll = vScroll;
        this.hScroll = hScroll;
    }

    @Override
    public void vertical(int min, int max, int val, int len) {
        vScroll.setMin(min);
        vScroll.setMax(max);
        vScroll.setValue(val);
        vScroll.setVisibleAmount(len);
    }

    @Override
    public void horizontal(double min, double max, double val, double len) {
        hScroll.setMin(min);
        hScroll.setMax(max);
        hScroll.setValue(val);
        hScroll.setVisibleAmount(len);
        hScroll.setVisible(max > len);
    }

    @Override
    public double xVal() {
        return hScroll.getValue();
    }

    public ScrollBar vScroll() {
        return vScroll;
    }

    public ScrollBar hScroll() {
        return hScroll;
    }

}
