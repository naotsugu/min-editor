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

import com.mammb.code.editor.ui.pane.EditorUpCall;
import com.mammb.code.editor.ui.pane.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The implementation of {@link EditorUpCall}.
 * @author Naotsugu Kobayashi
 */
public class AppEditorUpCall implements EditorUpCall {

    /** The path change listeners. */
    private final List<Consumer<PathChang>> pathChangedListeners = new ArrayList<>();

    /** The content modified listeners. */
    private final List<Consumer<ContentModifyChang>> contentModifiedListeners = new ArrayList<>();

    /**
     * PathChang record.
     * @param session the session
     * @param prevSession the previous session
     */
    record PathChang(Session session, Session prevSession) { }


    /**
     * ContentModifyChang record.
     * @param session the session
     * @param modified the modified
     */
    record ContentModifyChang(Session session, boolean modified) { }


    @Override
    public void pathChanged(Session session, Session prevSession) {
        pathChangedListeners.forEach(c -> c.accept(new PathChang(session, prevSession)));
    }

    @Override
    public void contentModified(Session session, boolean modified) {
        contentModifiedListeners.forEach(c -> c.accept(new ContentModifyChang(session, modified)));
    }


    /**
     * Add the path change listener.
     * @param consumer the path change listener
     */
    void addPathChangedListener(Consumer<PathChang> consumer) {
        pathChangedListeners.add(consumer);
    }


    /**
     * Add the content modified listener.
     * @param consumer the content modified listener
     */
    void addContentModifiedListener(Consumer<ContentModifyChang> consumer) {
        contentModifiedListeners.add(consumer);
    }

}
