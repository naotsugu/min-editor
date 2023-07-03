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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor2.model.layout.TextLine;
import java.util.List;

public interface TextList {

    List<TextLine> lines();

    /**
     * Scroll prev.
     * @param n the number of line
     * @return the number of scrolled lines
     */
    int prev(int n);

    /**
     * Scroll next.
     * @param n the number of line
     * @return the number of scrolled lines
     */
    int next(int n);


    boolean at(int row, int offset);

    /**
     * Get the line at head.
     * @return the line at head
     */
    default TextLine head() {
        return lines().get(0);
    }

    /**
     * Get the line at tail.
     * @return the line at tail
     */
    default TextLine tail() {
        List<TextLine> lines = lines();
        return lines.isEmpty() ? null : lines.get(lines.size() - 1);
    }

}