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
package com.mammb.code.editor.ui.app.control;

import javafx.scene.Parent;
import javafx.scene.Scene;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * The CssRun.
 * @author Naotsugu Kobayashi
 */
class CssRun {

    /** The empty css run. */
    static final CssRun empty = new CssRun("");

    /** The css text. */
    private final String cssText;


    /**
     * Constructor.
     * @param cssText the source css text
     */
    CssRun(String cssText) {
        this.cssText = Objects.requireNonNull(cssText);
    }


    /**
     * Join the css run.
     * @param other the css run
     * @return the joined css run
     */
    CssRun join(CssRun other) {
        return new CssRun(String.join(" ", cssText, other.cssText));
    }


    /**
     * Apply the stylesheet from this css text.
     * @param scene the scene
     */
    void into(Scene scene) {
        scene.getStylesheets().add(dataUrl());
    }


    /**
     * Apply the stylesheet from this css text.
     * @param parent the parent
     */
    void into(Parent parent) {
        parent.getStylesheets().add(dataUrl());
    }


    /**
     * Get the data url of css.
     * @return the data url of css
     */
    private String dataUrl() {
        return String.join(",",
            "data:text/css;base64",
            Base64.getEncoder().encodeToString(cssText.getBytes(StandardCharsets.UTF_8)));
    }

}
