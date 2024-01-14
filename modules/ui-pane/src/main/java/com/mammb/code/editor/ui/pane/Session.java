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

import java.nio.file.Path;

/**
 * Session.
 * @author Naotsugu Kobayashi
 */
public interface Session {

    Path path();

    int row();

    long caretIndex();

    default boolean isEmptyPath() {
        return path() == null;
    }

    default boolean isOriginPoint() {
        return row() == 0 && caretIndex() == 0;
    }

    record SessionRecord(Path path, int row, long caretIndex) implements Session {}

    static Session of(Path path) {
        return of(path, 0, 0);
    }

    static Session of(Path path, int row, long caretIndex) {
        return new SessionRecord(path, row, caretIndex);
    }

}
