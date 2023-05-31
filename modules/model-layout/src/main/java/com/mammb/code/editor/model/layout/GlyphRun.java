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
package com.mammb.code.editor.model.layout;

/**
 * GlyphRun.
 * @author Naotsugu Kobayashi
 */
public interface GlyphRun<F> {

    /**
     * The width of the {@code GlyphRun}.
     * @return the width of the {@code GlyphRun}
     */
    float width();

    /**
     * The height of the {@code GlyphRun}.
     * @return the height of the {@code GlyphRun}
     */
    float height();

    /**
     * The top-left location of the GlyphRun relative to
     * the origin of the Text Layout.
     */
    Point location();

    /**
     * Get the text for the span, can be empty but not {@code null}.
     * @return the text for the span, can be empty but not {@code null}
     */
    String getText();

    /**
     * The font for the span, if null the span is handled as embedded object.
     */
    FontFace<F> getFont();

    /**
     * The bounds for embedded object, only used the font returns null.
     * The text for a embedded object should be a single char ("\uFFFC" is recommended).
     */
    Bounds getBounds();

}
