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
package com.mammb.code.editor.ui.app.control;

import javafx.scene.Scene;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The CssRun.
 * @author Naotsugu Kobayashi
 */
class CssRun {

    static final CssRun empty = new CssRun("");

    private final String cssText;

    CssRun(String cssText) {
        this.cssText = cssText;
    }

    CssRun join(CssRun other) {
        return new CssRun(String.join(" ", cssText, other.cssText));
    }

    void into(Scene scene) {
        scene.getStylesheets().add("data:text/css;base64," +
            Base64.getEncoder().encodeToString(cssText.getBytes(StandardCharsets.UTF_8)));
    }

}
