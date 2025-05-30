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
package com.mammb.code.editor.core.layout;

import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.text.RowText;
import com.mammb.code.editor.core.text.Text;

/**
 * The content layout.
 * @author Naotsugu Kobayashi
 */
interface ContentLayout extends LineLayout {

    @Override
    RowText rowTextAt(int row);

    /**
     * Refresh the specified number of lines.
     * @param line the specified number of lines
     */
    void refresh(int line);

    /**
     * Refresh the range with the specified number of rows.
     * @param startRow the row number of start
     * @param endRow the row number of the end
     */
    void refreshAt(int startRow, int endRow);

    /**
     * Get the text list.
     * @param startLine the inclusive start line
     * @param endLine the exclusive end line
     * @return the text list
     */
    List<? extends Text> texts(int startLine, int endLine);

    /**
     * Get the {@link RowText} belonging to the specified number of lines.
     * @param line the specified number of lines
     * @return the {@link RowText}
     */
    RowText rowText(int line);

    /**
     * Get the location.
     * @param row the number of rows
     * @param col the number of columns
     * @param rangeLineStart the limitation of line
     * @param rangeLineEnd the limitation of line
     * @return the location
     */
    Optional<Loc> loc(int row, int col, int rangeLineStart, int rangeLineEnd);

    /**
     * Get the content.
     * @return the content
     */
    Content content();

}
