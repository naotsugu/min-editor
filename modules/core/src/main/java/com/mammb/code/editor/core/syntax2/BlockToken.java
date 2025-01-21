/*
 * Copyright 2022-2024 the original author or authors.
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
package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.syntax2.BlockType.BlockTypeWith;

public interface BlockToken {

    BlockType type();

    /**
     * Composite interface {@link With}.
     * @param <T>
     */
    interface BlockTokenWith<T> extends BlockToken, With<T> {
        T with();
    }

    interface Open extends BlockToken {}
    interface Close extends BlockToken {}
    interface OpenWith<T> extends Open, BlockTokenWith<T> {}

    static Open open(BlockType type) {
        record OpenRecord(BlockType type) implements Open {}
        return new OpenRecord(type);
    }

    static Close close(BlockType type) {
        record CloseRecord(BlockType type) implements Close {}
        return new CloseRecord(type);
    }

    static <T> OpenWith<T> open(BlockTypeWith<T> type, T with) {
        record OpenWithRecord<T>(BlockTypeWith<T> type, T with) implements OpenWith<T> {}
        return new OpenWithRecord<>(type, with);
    }


}
