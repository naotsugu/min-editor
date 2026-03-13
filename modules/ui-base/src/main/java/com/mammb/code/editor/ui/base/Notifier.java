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

import java.util.ArrayList;
import java.util.List;

/**
 * The Notifier class is responsible for managing a list of listeners
 * and notifying them when an event occurs. It provides methods to
 * add and remove listeners, as well as to trigger notifications.
 * @author Naotsugu Kobayashi
 */
public class Notifier {

    /** The listeners */
    private final List<NotifyListener> listeners = new ArrayList<>();

    /**
     * Adds a listener to the list of listeners that will be notified of events.
     * @param listener the {@code NotifyListener} to add to the list of listeners.
     */
    public void addListener(NotifyListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener from the list of registered listeners.
     * If the listener is not found in the list, no action is taken.
     * @param listener the {@code NotifyListener} to be removed from the list of listeners.
     * @return {@code true} if the listener was successfully removed; {@code false} otherwise.
     */
    public boolean removeListener(NotifyListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Sends a notification to all registered listeners with the provided message and description.
     * @param message the title or headline of the notification to be sent to the listeners
     * @param description the detailed description or content of the notification
     */
    public void send(String message, String description) {
        listeners.forEach(l -> l.accept(message, description));
    }

    /**
     * Sends a notification to all registered listeners with the specified message.
     * This method acts as shorthand for sending notifications where
     * the description is an empty string.
     * @param message the title or headline of the notification to be sent to the listeners
     */
    public void send(String message) {
        listeners.forEach(l -> l.accept(message, ""));
    }

}
