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
package com.mammb.code.javafx.scene.text.impl;

import com.mammb.code.editor.model.layout.Bounds;
import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor.model.layout.TextLine;
import javafx.scene.text.Font;

/**
 * LayoutLine.
 * @author Naotsugu Kobayashi
 */
public class TextLineImpl implements TextLine<Font> {

    /** The peer. */
    private final com.sun.javafx.scene.text.TextLine peer;

    /** The array of GlyphRun. */
    private final GlyphRun<Font>[] runs;


    /**
     * Constructor.
     * @param textLine the TextLine of javafx
     * @param runs the array of GlyphRun
     */
    public TextLineImpl(
            com.sun.javafx.scene.text.TextLine textLine,
            GlyphRun<Font>[] runs) {
        this.peer = textLine;
        this.runs = runs;
    }


    @Override
    public GlyphRun<Font>[] runs() {
        return runs;
    }

    @Override
    public Bounds bounds() {
        return RectBounds.of(peer.getBounds());
    }

    @Override
    public float getLeftSideBearing() {
        return peer.getLeftSideBearing();
    }

    @Override
    public float getRightSideBearing() {
        return peer.getRightSideBearing();
    }

    @Override
    public int startOffset() {
        return peer.getStart();
    }

    @Override
    public int length() {
        return peer.getLength();
    }

}
