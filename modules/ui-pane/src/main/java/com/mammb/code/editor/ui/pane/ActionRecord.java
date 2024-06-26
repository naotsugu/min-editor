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
 * The action event record.
 * @param type the action event type
 * @param attr the action event attribute
 * @param occurredAt the action event occurred at
 */
record ActionRecord(
    Action.Type type,
    String attr,
    long occurredAt
) implements Action {

    /**
     * Create a new action event.
     * @param type the action event type
     */
    ActionRecord(Action.Type type) {
        this(type, null, System.currentTimeMillis());
    }

    /**
     * Create a new action event.
     * @param type the action event type
     * @param attr the action event attribute
     */
    ActionRecord(Action.Type type, String attr) {
        this(type, attr, System.currentTimeMillis());
    }

}
