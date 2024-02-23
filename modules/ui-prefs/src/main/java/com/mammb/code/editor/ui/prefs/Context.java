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
package com.mammb.code.editor.ui.prefs;

import com.mammb.code.editor.ui.prefs.impl.ContextImpl;

/**
 * Context.
 * @author Naotsugu Kobayashi
 */
public interface Context {

    /**
     * Get the preference.
     * @return the preference
     */
    Preference preference();

    /**
     * Get the region width.
     * @return the region width
     */
    double regionWidth();

    /**
     * Get the region height.
     * @return the region height
     */
    double regionHeight();

    /**
     * Create a new context.
     * @return a new context
     */
    static Context of() {
        return new ContextImpl();
    }

}
