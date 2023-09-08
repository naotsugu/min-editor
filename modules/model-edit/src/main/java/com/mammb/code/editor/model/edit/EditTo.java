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
package com.mammb.code.editor.model.edit;

import com.mammb.code.editor.model.text.OffsetPoint;

/**
 * EditTo.
 * @author Naotsugu Kobayashi
 */
public interface EditTo {

    /**
     * Inserts the char sequence into at the specified point.
     * @param point the offset
     * @param text a char sequence
     */
    void insert(OffsetPoint point, String text);

    /**
     * Removes the characters in a substring of at the specified point.
     * @param point the beginning index, inclusive
     * @param text the length to be deleted
     */
    void delete(OffsetPoint point, String text);

}
