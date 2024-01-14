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
 * The AppEditorUpCall.
 * @author Naotsugu Kobayashi
 */
public class AppEditorUpCall implements EditorUpCall {

    /** The path change handlers. */
    private final List<Consumer<PathChang>> pathChangedHandlers = new ArrayList<>();

    /** The content modified handlers. */
    private final List<Consumer<ContentModifyChang>> contentModifyHandlers = new ArrayList<>();

    record PathChang(Session session, Session prevSession) { }
    record ContentModifyChang(Session session, boolean modified) { }

    @Override
    public void pathChanged(Session session, Session prevSession) {
        pathChangedHandlers.forEach(c -> c.accept(new PathChang(session, prevSession)));
    }

    @Override
    public void contentModified(Session session, boolean modified) {
        contentModifyHandlers.forEach(c -> c.accept(new ContentModifyChang(session, modified)));
    }

    void onPathChanged(Consumer<PathChang> consumer) {
        pathChangedHandlers.add(consumer);
    }

    void onContentModified(Consumer<ContentModifyChang> consumer) {
        contentModifyHandlers.add(consumer);
    }

}
