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
package com.mammb.code.editor.core;

/**
 * Represents a value object for a name, providing different forms of its representation.
 * This interface focuses solely on the various textual forms of a name.
 * @author Naotsugu Kobayashi
 */
public interface Name {

    /**
     * Returns the canonical (standardized, official, or complete) representation of the name.
     * This is typically the most precise and unambiguous form.
     * <p>Example: Absolute path for a file, full URL for a web page.</p>
     *
     * @return The canonical string representation.
     */
    String canonical();

    /**
     * Returns the plain, unformatted representation of the name.
     * This form is typically not for direct UI display where specific formatting is required.
     * <p>Example: File name including extension, HTML page title.</p>
     *
     * @return The plain, unformatted string representation.
     */
    String plain();

    /**
     * Returns a concise name optimized for a specific context. This might include UI display
     * (like a tab title) or other scenarios requiring a more compact or user-friendly form
     * than the canonical representation. This form does not include state-dependent modifications
     * (e.g., a leading '*' for unsaved changes); such modifications should be applied by the consumer of this name.
     * <p>Example: File name, HTML page title.</p>
     *
     * @return A string optimized for a specific context.
     */
    String contextual();

    Name EMPTY = new Name() {
        @Override public String canonical() { return ""; }
        @Override public String plain() { return ""; }
        @Override public String contextual() { return ""; }
    };

}
