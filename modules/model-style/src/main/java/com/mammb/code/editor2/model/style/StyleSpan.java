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
package com.mammb.code.editor2.model.style;

import java.util.Objects;

/**
 * StyleSpan.
 * @param style the style
 * @param point the point of start
 * @param length the length of style
 * @author Naotsugu Kobayashi
 */
public record StyleSpan(Style style, int point, int length) {

    public StyleSpan {
        if (length < 0 ||
            (length == 0 && !(style instanceof Style.ZeroLength)))
            throw new IllegalArgumentException();
        Objects.requireNonNull(style);
    }

    /**
     * Constructor.
     * @param style the style
     * @param point the point of start
     */
    public StyleSpan(Style.ZeroLength style, int point) {
        this(style, point, 0);
    }

}
