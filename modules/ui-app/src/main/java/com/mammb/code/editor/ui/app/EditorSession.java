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

import com.mammb.code.editor.ui.pane.Session;
import javafx.beans.property.BooleanProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The EditorSession.
 * @author Naotsugu Kobayashi
 */
public class EditorSession {

    /** The session list. */
    private final List<Session> histories = new ArrayList<>();

    /** The current session. */
    private Session current;

    /** The forward disable property. */
    private BooleanProperty forwardDisableProperty;

    /** The backward disable property. */
    private BooleanProperty backwardDisableProperty;


    /**
     * Push session.
     * @param session the session
     * @param prev the previous session
     */
    public void push(Session session, Session prev) {

        if (session == null || session.isEmptyPath()) return;
        if (current != null && session.path().equals(current.path())) {
            applyPointInHistories(prev);
            return;
        }

        var currentIndex = histories.indexOf(current);
        if (currentIndex != histories.size() - 1) {
            histories.subList(currentIndex + 1, histories.size()).clear();
        }
        applyPointInHistories(prev);
        histories.add(session);

        current = session;
        ensureProperty();

    }


    /**
     * Replace the same path session from the history.
     * @param session the session
     */
    private void applyPointInHistories(Session session) {
        if (session == null || session.isEmptyPath()) {
            return;
        }
        for (var i = histories.size() - 1; i >= 0; i--) {
            var s = histories.get(i);
            if (Objects.equals(s.path(), session.path())) {
                histories.remove(i);
                histories.add(i, Session.of(s.path(), session.row(), session.caretIndex()));
                break;
            }
        }
    }


    /**
     * Forward history.
     * @return the session
     */
    public Session forward() {
        var currentIndex = histories.indexOf(current);
        if (currentIndex == histories.size() - 1) {
            return current;
        }
        current = histories.get(currentIndex + 1);
        ensureProperty();
        return current;
    }


    /**
     * Backward history.
     * @return the session
     */
    public Session backward() {
        var currentIndex = histories.indexOf(current);
        if (currentIndex == 0) {
            return current;
        }
        current = histories.get(currentIndex - 1);
        ensureProperty();
        return current;
    }


    /**
     * Set the forward disable property.
     * @param property the forward disable property
     */
    public void setForwardDisableProperty(BooleanProperty property) {
        this.forwardDisableProperty = property;
    }


    /**
     * Set the backward disable property.
     * @param property the backward disable property
     */
    public void setBackwardDisableProperty(BooleanProperty property) {
        this.backwardDisableProperty = property;
    }


    /**
     * Ensure property.
     */
    private void ensureProperty() {
        if (current == null) {
            forwardDisableProperty.set(true);
            backwardDisableProperty.set(true);
        } else {
            forwardDisableProperty.set(histories.indexOf(current) >= histories.size() - 1);
            backwardDisableProperty.set(histories.indexOf(current) <= 0);
        }
    }

}
