/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.core;

/**
 * The Clipboard.
 * @author Naotsugu Kobayashi
 */
public interface Clipboard {

    /**
     * Put the string to the clipboard.
     * @param text the string
     */
    void setPlainText(String text);

    /**
     * Get the string from the clipboard.
     * @return the string
     */
    String getString();

    /**
     * Get the html string from the clipboard.
     * @return the html string
     */
    String getHtml();

    /**
     * Get whether the clipboard has content.
     * @return {@code true}, if the clipboard has content
     */
    boolean hasContents();

}
