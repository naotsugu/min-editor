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
package com.mammb;

/**
 * TextRun.
 * @author Naotsugu Kobayashi
 */
public interface TextRun {

    /**
     * Get the layout.
     * @return the layout
     */
    Layout layout();

    /**
     * Get the text.
     * @return the text
     */
    String text();

    /**
     * Get the style.
     * @return the style
     */
    Style style();

    /**
     * Create a new TextRun.
     * @param layout the layout
     * @param text the text
     * @param style the style
     * @return a created TextRun
     */
    static TextRun of(Layout layout, String text, Style style) {
        record TextRunRecord(Layout layout, String text, Style style) implements TextRun { }
        return new TextRunRecord(layout, text, style);
    }

}
