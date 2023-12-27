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
package com.mammb.code.editor.ui.app;

import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * The AppKeys.
 * @author Naotsugu Kobayashi
 */
public class AppKeys {

    /** Shortcut n. */
    public static final KeyCombination SC_N = new KeyCharacterCombination("n", KeyCombination.SHORTCUT_DOWN);
    /** Shortcut f. */
    public static final KeyCombination SC_F = new KeyCharacterCombination("f", KeyCombination.SHORTCUT_DOWN);
    /** Shortcut right. */
    public static final KeyCombination SC_FORWARD  = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);
    /** Shortcut left. */
    public static final KeyCombination SC_BACKWARD = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

}
