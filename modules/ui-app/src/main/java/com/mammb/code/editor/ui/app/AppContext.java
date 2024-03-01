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
package com.mammb.code.editor.ui.app;

import com.mammb.code.editor.ui.prefs.Context;
import com.mammb.code.editor.ui.prefs.Preference;
import javafx.application.Application;

/**
 * The AppContext.
 * @author Naotsugu Kobayashi
 */
public class AppContext implements Context {

    /** The Preference. */
    private Preference preference = Preference.of();

    /** The width. */
    public double width;

    /** The height. */
    public double height;


    /**
     * Constructor.
     * @param width the width of window
     * @param height the height of window
     */
    public AppContext(double width, double height) {
        this.width = width;
        this.height = height;
    }


    /**
     * Create a new Context.
     * @param params the parameter
     * @return a new Context
     */
    public static AppContext of(Application.Parameters params) {
        return new AppContext(800, 480);
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
