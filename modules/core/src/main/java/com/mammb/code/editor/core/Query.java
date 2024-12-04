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
package com.mammb.code.editor.core;

import com.mammb.code.editor.core.model.QueryRecords.*;
import java.nio.file.Path;

/**
 * The Editor model query.
 * @param <R> the type of query result
 * @author Naotsugu Kobayashi
 */
public interface Query<R> {

    Query<String> rowEndingSymbol = new RowEndingSymbol();
    Query<String> charsetSymbol = new CharsetSymbol();
    Query<Boolean> modified = new Modified();
    Query<byte[]> bom = new Bom();
    Query<Point> caretPoint = new CaretPoint();
    Query<Path> contentPath = new ContentPath();

    Query<Integer> widthAsCharacters = new WidthAsCharacters();

}
