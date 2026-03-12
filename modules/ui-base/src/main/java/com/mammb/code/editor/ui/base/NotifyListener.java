/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.ui.base;

/**
 * Represents a listener interface for receiving notification events.
 * Implementations of this interface can process notifications
 * when the notify method is invoked.
 * @author Naotsugu Kobayashi
 */
public interface NotifyListener {

    /**
     * Sends a notification with a specified headline and text content.
     *
     * @param headline the title or headline of the notification
     * @param text the detailed text content of the notification
     */
    void notify(String headline, String text);

}
