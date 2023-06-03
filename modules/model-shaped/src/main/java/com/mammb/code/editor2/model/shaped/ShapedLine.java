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
package com.mammb.code.editor2.model.shaped;

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.core.PointText;
import java.util.List;

/**
 * ShapedLine.
 * @param <F> the type of font
 * @param <C> the type of color
 * @author Naotsugu Kobayashi
 */
public interface ShapedLine<F, C> extends PointText {

    @Override
    String text();

    @Override
    OffsetPoint point();

    /**
     * Get the list of ShapedRun in the line.
     * The list is visually ordered.
     * @return the list of ShapedRun in the line
     */
    List<? extends ShapedRun<F, C>> runs();

    /**
     * Gets the line length in character.
     * @return the line length in character
     */
    default int length() {
        return text().length();
    }

}
