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

import com.mammb.code.editor.ui.model.ModelQuery;

/**
 * The EditorDownCall.
 * EditorDownCall is the interface for operation requests from
 * the application's parent UI to the editor pane.
 * <pre>
 *     ------------------------------
 *     |             App            |
 *     ------------------------------
 *        | EditorDownCall      ↑
 *        |                     |
 *        ↓                     | EditorUpCall
 *     ------------------------------
 *     |        Editor Pane         |
 *     ------------------------------
 * </pre>
 * @author Naotsugu Kobayashi
 */
public interface EditorDownCall {

    /**
     * Requests a path change to the model.
     * @param session the new session
     */
    void requestPathChange(Session session);

    /**
     * Requests find to the model.
     * @param regexp the string of regexp
     */
    void requestFind(String regexp, boolean forward);

    /**
     * Request focus.
     */
    void requestFocus();

    /**
     * Request select clear.
     */
    void requestSelectClear();

    /**
     * Request key action.
     * @param action key action
     */
    void requestKeyAction(Action action);

    /**
     * Request query.
     * @param query the query
     */
    <T> void requestQuery(Query<T> query);


    static Query<String> selectedText() {
        return new Query<>() {
            @Override ModelQuery<String> modelQuery() {
                return ModelQuery.selectedText;
            }
        };
    }


    abstract class Query<T> {
        private T ret;
        public void ret(T ret) {
            this.ret = ret;
        }
        public T ret() {
            return ret;
        }
        abstract ModelQuery<T> modelQuery();
    }

}
