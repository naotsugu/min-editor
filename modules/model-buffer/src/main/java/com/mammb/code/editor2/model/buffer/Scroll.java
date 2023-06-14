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
package com.mammb.code.editor2.model.buffer;

/**
 * Scroll.
 * @author Naotsugu Kobayashi
 */
public interface Scroll {

    /**
     * Scroll previous line.
     * @param n the number of line
     */
    void prev(int n);

    /**
     * Scroll next line.
     * @param n the number of line
     */
    void next(int n);


    /**
     * Scroll previous line.
     */
    default void prev() {
        prev(1);
    }

    /**
     * Scroll next line.
     */
    default void next() {
        next(1);
    }

}
