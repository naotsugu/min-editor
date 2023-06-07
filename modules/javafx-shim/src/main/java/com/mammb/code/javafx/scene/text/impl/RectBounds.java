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

/**
 * RectBoundsAdapter.
 * @author Naotsugu Kobayashi
 */
public class RectBounds implements Bounds {

    private final com.sun.javafx.geom.RectBounds peer;

    private RectBounds(com.sun.javafx.geom.RectBounds rectBounds) {
        this.peer = rectBounds;
    }

    public static RectBounds of(com.sun.javafx.geom.RectBounds rectBounds) {
        return new RectBounds(rectBounds);
    }

    @Override
    public float minX() {
        return peer.getMinX();
    }

    @Override
    public float minY() {
        return peer.getMinY();
    }

    @Override
    public float maxX() {
        return peer.getMaxX();
    }

    @Override
    public float maxY() {
        return peer.getMaxX();
    }

}

