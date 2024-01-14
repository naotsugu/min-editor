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
 * The EditorUpCall.
 * @author Naotsugu Kobayashi
 */
public interface EditorUpCall {

    /**
     * Notify of content path change.
     * @param session the session
     * @param prevSession the previous session
     */
    void pathChanged(Session session, Session prevSession);

    /**
     * Notify of content changes.
     * @param session the session
     * @param modified edited or not
     */
    void contentModified(Session session, boolean modified);

    /**
     * The empty editor up call.
     * @return the empty editor up call
     */
    static EditorUpCall empty() {
        return new EditorUpCall() {
            @Override public void pathChanged(Session session, Session prevSession) { }
            @Override public void contentModified(Session session, boolean modified) { }
        };
    }

}
