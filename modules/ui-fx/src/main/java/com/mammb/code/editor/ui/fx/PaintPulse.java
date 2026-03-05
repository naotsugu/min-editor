/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.ui.fx;

import javafx.animation.AnimationTimer;

/**
 * The PaintPulse class is an extension of AnimationTimer that is used
 * to optimize and control rendering operations during an animation cycle.
 * It ensures that a specified paint action is executed when requested
 * and only runs the paint operation if marked as dirty, avoiding unnecessary
 * execution of redundant operations.
 * @author Naotsugu Kobayashi
 */
public class PaintPulse extends AnimationTimer {

    private volatile boolean dirty = false;

    private final Runnable paint;

    /**
     * Constructor.
     * @param paint the paint runnable
     */
    public PaintPulse(Runnable paint) {
        this.paint = paint;
    }

    /**
     * Marks the animation as dirty, indicating that the paint action should
     * be executed during the next handle call in the animation cycle.
     */
    public void request() {
        dirty = true;
    }

    @Override
    public void handle(long now) {
        if (dirty) {
            paint.run();
            dirty = false;
        }
    }

}
