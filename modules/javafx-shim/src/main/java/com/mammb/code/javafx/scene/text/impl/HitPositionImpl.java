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

import com.mammb.code.editor.model.layout.HitPosition;
import com.sun.javafx.scene.text.TextLayout;

/**
 * Represents the hit information in the text plane.
 * @param charIndex the index of the character which this hit information refers to
 * @param insertionIndex the index of the insertion position
 * @param leading the indicates whether the hit is on the leading edge of the character
 * @author Naotsugu Kobayashi
 */
public record HitPositionImpl(
    int charIndex,
    int insertionIndex,
    boolean leading) implements HitPosition {

    public HitPositionImpl(TextLayout.Hit hit) {
        this(hit.getCharIndex(), hit.getInsertionIndex(), hit.isLeading());
    }

}
