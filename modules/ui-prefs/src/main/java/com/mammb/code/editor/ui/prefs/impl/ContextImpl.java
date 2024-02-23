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

    /** The preference. */
    private Preference preference;

    /** The width. */
    private double width;

    /** The height. */
    private double height;

    /**
     * Constructor.
     * @param preference the preference
     * @param width the width
     * @param height the height
     */
    private ContextImpl(Preference preference, double width, double height) {
        this.preference = preference;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor.
     */
    public ContextImpl() {
        this(PreferenceImpl.of(), 800, 400);
    }


    @Override
    public Preference preference() {
        return preference;
    }

    @Override
    public double regionWidth() {
        return width;
    }

    @Override
    public double regionHeight() {
        return height;
    }

}
