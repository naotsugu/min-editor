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

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import java.util.List;
import com.mammb.code.editor.core.text.ColsText;

/**
 * The CSV Layout.
 * @author Naotsugu Kobayashi
 */
public class CsvLayout extends RowLayout {

    /**
     * Constructor.
     * @param content the content
     * @param fm the font metrics
     */
    public CsvLayout(Content content, FontMetrics fm) {
        super(content, fm);
    }

    @Override
    public ColsText rowTextAt(int row) {
        return null; // TODO
    }

    @Override
    public List<? extends ColsText> texts(int startLine, int endLine) {
        return texts(startLine, endLine);
    }

    @Override
    public ColsText rowText(int line) {
        return rowTextAt(line);
    }

}
