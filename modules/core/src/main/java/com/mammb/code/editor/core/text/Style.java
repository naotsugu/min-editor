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
package com.mammb.code.editor.core.text;

/**
 * The style.
 * @author Naotsugu Kobayashi
 */
public sealed interface Style {

    /**
     * The colored style.
     */
    sealed interface ColoredStyle extends Style {
        String colorString();
    }

    /** The text color style. */
    record TextColor(String colorString) implements ColoredStyle { }
    /** The background color style. */
    record BgColor(String colorString) implements ColoredStyle { }
    /** The under color style. */
    record UnderColor(String colorString) implements ColoredStyle { }

    /** The style span. */
    record StyleSpan(Style style, int offset, int length) { }

}
