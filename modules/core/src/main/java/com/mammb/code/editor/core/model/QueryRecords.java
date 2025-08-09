/*
 * Copyright 2023-2025 the original author or authors.
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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Optional;
import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Pair;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;

/**
 * The query records.
 * @author Naotsugu Kobayashi
 */
public interface QueryRecords {

    /** The query of a row ending symbol. */
    record RowEndingSymbol() implements Query<String> { }
    /** The query of a charset. */
    record CharCode() implements Query<Charset> { }
    /** The query of a charset symbol. */
    record CharCodeSymbol() implements Query<String> { }
    /** The query of modified. */
    record Modified() implements Query<Boolean> { }
    /** The query of bom. */
    record Bom() implements Query<byte[]> { }
    /** The query of caret point. */
    record CaretPoint() implements Query<Point> { }
    /** The query of a content-path. */
    record ContentPath() implements Query<Optional<Path>> { }
    /** The last modified time. */
    record LastModifiedTime() implements Query<Optional<FileTime>> { }
    /** The query of a content-name. */
    record ModelName() implements Query<Name> { }
    /** The query of width as characters. */
    record WidthAsCharacters() implements Query<Integer> { }
    /** The query of founded count. */
    record FoundCounts() implements Query<Integer> { }
    /** The query of the selected count. */
    record SelectedCounts() implements Query<Integer> { }
    /** The query of line size. */
    record LineSize() implements Query<Integer> { }
    /** The query of row size. */
    record RowSize() implements Query<Integer> { }
    /** The query of content size. */
    record Size() implements Query<Long> { }
    /** The query of has selected. */
    record HasSelected() implements Query<Boolean> { }
    /** The query of selected text. */
    record SelectedText() implements Query<String> { }
    /** The query of char at caret. */
    record CharAtCaret() implements Query<Pair<String>> { }
    /** The query of bytes at caret. */
    record BytesAtCaret() implements Query<byte[]> { }

}
