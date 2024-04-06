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
package com.mammb.code.editor.ui.pane;

/**
 * The action event.
 * @author Naotsugu Kobayashi
 */
public interface KeyActionEvent {

    /**
     * Get the action type.
     * @return the type
     */
    Keys.Action type();

    /**
     * Get occurred at.
     * @return occurred at
     */
    long occurredAt();

    static KeyActionEvent of(Keys.Action type) {
        return new KeyActionEventRecord(type);
    }

}