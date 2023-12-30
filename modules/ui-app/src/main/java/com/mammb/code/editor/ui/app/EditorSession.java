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

    private final List<Session> histories = new ArrayList<>();

    private Session current;

    private BooleanProperty forwardDisableProperty;
    private BooleanProperty backwardDisableProperty;

    private void push(Session session) {
        if (session == null || session.isEmptyPath()) return;
        if (current != null && session.path().equals(current.path())) return;

        var currentIndex = histories.indexOf(current);
        if (currentIndex != histories.size() - 1) {
            histories.subList(currentIndex + 1, histories.size()).clear();
        }
        histories.add(session);
        current = session;
        ensureProperty();
    }

    public void push(Session session, Session prev) {
        if (session == null || session.isEmptyPath()) return;
        if (current != null && session.path().equals(current.path())) return;

        var currentIndex = histories.indexOf(current);
        if (currentIndex != histories.size() - 1) {
            histories.subList(currentIndex + 1, histories.size()).clear();
        }
        histories.add(session);

        for (var i = histories.size() - 1; i >= 0; i--) {
            var s = histories.get(i);
            if (Objects.equals(s.path(), prev.path())) {
                histories.remove(i);
                histories.add(i, Session.of(s.path(), prev.row(), prev.caretIndex()));
                break;
            }
        }

        current = session;
        ensureProperty();

    }

    public Session forward() {
        var currentIndex = histories.indexOf(current);
        if (currentIndex == histories.size() - 1) {
            return current;
        }
        current = histories.get(currentIndex + 1);
        ensureProperty();
        return current;
    }

    public Session backward() {
        var currentIndex = histories.indexOf(current);
        if (currentIndex == 0) {
            return current;
        }
        current = histories.get(currentIndex - 1);
        ensureProperty();
        return current;
    }

    public void setForwardDisableProperty(BooleanProperty property) {
        this.forwardDisableProperty = property;
    }

    public void setBackwardDisableProperty(BooleanProperty property) {
        this.backwardDisableProperty = property;
    }

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
