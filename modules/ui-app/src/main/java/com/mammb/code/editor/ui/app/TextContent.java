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
package com.mammb.code.editor.ui.app;

import javafx.beans.value.ObservableStringValue;

/**
 * Interface representing a text input's content.
 * @author Naotsugu Kobayashi
 */
public interface TextContent extends ObservableStringValue {

    /**
     * Retrieves a subset of the content.
     * @param start the start
     * @param end the end
     * @return a subset of the content
     */
    String get(int start, int end);

    /**
     * Inserts a sequence of characters into the content.
     * @param index the index
     * @param text the text string
     * @param notifyListeners the notify listener flag
     */
    void insert(int index, String text, boolean notifyListeners);

    /**
     * Removes a sequence of characters from the content.
     *
     * @param start the start
     * @param end the end
     * @param notifyListeners the notify listener flag
     */
    void delete(int start, int end, boolean notifyListeners);

    /**
     * Returns the number of characters represented by the content.
     * @return the number of characters
     */
    int length();

}
