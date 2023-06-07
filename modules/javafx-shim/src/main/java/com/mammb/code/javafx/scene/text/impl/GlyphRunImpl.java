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

import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor.model.layout.Point;
import com.sun.javafx.scene.text.GlyphList;
import javafx.scene.text.Font;

/**
 * GlyphRunImpl.
 * @author Naotsugu Kobayashi
 */
public class GlyphRunImpl implements GlyphRun<Font> {

    private final GlyphList peer;
    private final String text;
    private final Font font;

    public GlyphRunImpl(GlyphList peer, String text, Font font) {
        this.peer = peer;
        this.text = text;
        this.font = font;
    }

    protected GlyphList pear() {
        return peer;
    }

    @Override
    public int glyphCount() {
        return peer.getGlyphCount();
    }

    @Override
    public float posX(int glyphIndex) {
        return peer.getPosX(glyphIndex);
    }

    @Override
    public float posY(int glyphIndex) {
        return peer.getPosY(glyphIndex);
    }

    @Override
    public int charOffset(int glyphIndex) {
        return peer.getCharOffset(glyphIndex);
    }

    @Override
    public float width() {
        return peer.getWidth();
    }

    @Override
    public float height() {
        return peer.getHeight();
    }

    @Override
    public Point location() {
        return new PointImpl(peer.getLocation());
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Font getFont() {
        return font;
    }

}
