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
package com.mammb.code.editor.model.text;

import java.util.List;

/**
 * The textual scroll.
 * @author Naotsugu Kobayashi
 */
public interface TextualScroll<T extends Textual> extends TextualList<T> {

    @Override
    List<T> texts();

    /**
     * Get the page size.
     * @return the page size
     */
    int pageSize();

    /**
     * Set the page size.
     * @param size the page size
     */
    void setPageSize(int size);

    /**
     * Scroll previous line.
     * @param n the number of line
     * @return the added lines
     */
    List<T> prev(int n);

    /**
     * Scroll next line.
     * @param n the number of line
     * @return the added lines
     */
    List<T> next(int n);

    /**
     * Scroll.
     * @param rowDelta
     */
    boolean move(int rowDelta);

}
