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

import com.mammb.code.editor.core.Session;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * The SessionRecord.
 * @param path the path
 * @param lastModifiedTime the last modified time
 * @param caretRow the row index at the caret
 * @param caretCol the column index at the caret
 * @param timestamp the timestamp of this session
 * @author Naotsugu Kobayashi
 */
record SessionRecord(
    Path path,
    FileTime lastModifiedTime,
    int caretRow,
    int caretCol,
    long timestamp) implements Session {

}
