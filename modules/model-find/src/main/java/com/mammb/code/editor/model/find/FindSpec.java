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
package com.mammb.code.editor.model.find;

import com.mammb.code.editor.model.find.impl.FindSpecImpl;

/**
 * The find specification.
 * @author Naotsugu Kobayashi
 */
public interface FindSpec {

    /**
     *
     * @param text
     * @return
     */
    Match[] match(String text);

    /**
     * Gets whether it is empty or not.
     * @return {@code true} if spec is empty
     */
    boolean isEmpty();

    /**
     * Get whether it is a one-shot find.
     * @return {@code true} if it is a one-shot find
     */
    boolean once();

    /**
     * Get whether it is a forward find.
     * @return {@code true} if it is a forward find
     */
    boolean forward();

    /**
     * Create a new find spec.
     * @param string the string for find
     * @param forward whether it is a forward find
     * @return find spec
     */
    static FindSpec of(String string, boolean forward) {
        return new FindSpecImpl(string, true, forward);
    }

    /**
     * Create a new all search spec.
     * @param string the string for find
     * @return all search spec
     */
    static FindSpec allOf(String string) {
        return new FindSpecImpl(string, false, true);
    }

}
