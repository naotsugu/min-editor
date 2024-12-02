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

import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;

/**
 * The query records.
 * @author Naotsugu Kobayashi
 */
public interface QueryRecords {
    record RowEndingSymbol() implements Query<String> { }
    record CharsetSymbol() implements Query<String> { }
    record Modified() implements Query<Boolean> {}
    record Bom() implements Query<byte[]> {}
    record CaretPoint() implements Query<Point> {}

    record WidthAsCharacters() implements Query<Integer> {}
}
