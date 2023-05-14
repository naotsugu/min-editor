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
package com.mammb.code.editor2.model.core;

/**
 * PointText.
 * @author Naotsugu Kobayashi
 */
public interface PointText {

    /**
     * Get the offset point.
     * @return the offset point.
     */
    OffsetPoint point();

    /**
     * Get the text string.
     * @return the text string.
     */
    String text();

    /**
     * Get the offset of tail.
     * @return the offset of tail.
     */
    default int tailOffset() {
        return point().offset() + text().length();
    }

    /**
     * Get the coed point offset of tail.
     * @return the coed point offset of tail.
     */
    default int tailCpOffset() {
        return point().cpOffset() + Character.codePointCount(text(), 0, text().length());
    }

    /**
     * Create a new PointText
     * @param point the offset point
     * @param text the text string
     * @return a new PointText
     */
    static PointText of(OffsetPoint point, String text) {
        return new com.mammb.code.editor2.model.core.impl.PointText(point, text);
    }

}
