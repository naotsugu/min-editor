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
package com.mammb.code.editor.core.model;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * The Session.
 * @author Naotsugu Kobayashi
 */
public class Session {

    /** The path. */
    private Path path;
    /** The last modified time. */
    private FileTime lastModifiedTime;
    /** the row index at the caret. */
    private int row;
    /** the column index at the caret. */
    private int col;
    /** The timestamp of this session. */
    private long timestamp;

    /**
     * Get the path.
     * @return the path
     */
    public Path path() {
        return path;
    }

    /**
     * Get the row index at the caret(zero-origin).
     * @return the row index at the caret
     */
    public int caretRow() {
        return row;
    }

    /**
     * Get the column index at the caret.
     * @return the column index at the caret
     */
    public int caretCol() {
        return col;
    }

}
