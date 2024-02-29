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
package com.mammb.code.editor.ui.model;

/**
 * ModelQuery.
 * @author Naotsugu Kobayashi
 */
public sealed interface ModelQuery<R> permits
        ModelQuery.SelectedText,
        ModelQuery.CurrentLineText,
        ModelQuery.CurrentRowText,
        ModelQuery.CaretBeforeText {

    /**
     * The query result.
     * @param <R> the type of result
     */
    interface Result<R> {
        ModelQuery<R> query();
        R value();
    }

    default Result<R> on(R value) {
        record ResultRecord<R>(ModelQuery<R> query, R value) implements Result<R> {}
        return new ResultRecord<>(this, value);
    }

    record SelectedText() implements ModelQuery<String> { }
    record CurrentLineText() implements ModelQuery<String> { }
    record CurrentRowText() implements ModelQuery<String> { }
    record CaretBeforeText() implements ModelQuery<String> { }

    static SelectedText selectedText() {
        return new SelectedText();
    }
    static CurrentLineText currentLineText() {
        return new CurrentLineText();
    }
    static CurrentRowText currentRowText() {
        return new CurrentRowText();
    }
    static CaretBeforeText caretBeforeText() {
        return new CaretBeforeText();
    }

}
