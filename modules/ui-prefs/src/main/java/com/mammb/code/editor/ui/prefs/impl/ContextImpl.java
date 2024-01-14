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
package com.mammb.code.editor.ui.prefs.impl;

import com.mammb.code.editor.ui.prefs.Context;
import com.mammb.code.editor.ui.prefs.Preference;

/**
 * Context.
 * @author Naotsugu Kobayashi
 */
public class ContextImpl implements Context {

    private Preference preference = PreferenceImpl.of();
    private double nodeWidth = 800;
    private double nodeHeight = 480;

    @Override
    public Preference preference() {
        return preference;
    }

    @Override
    public double regionWidth() {
        return nodeWidth;
    }

    @Override
    public double regionHeight() {
        return nodeHeight;
    }
}
