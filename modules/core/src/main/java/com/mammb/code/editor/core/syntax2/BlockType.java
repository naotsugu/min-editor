/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.function.Function;

/**
 * The block type.
 * @author Naotsugu Kobayashi
 */
public interface BlockType {

    String open();

    String close();

    interface BlockTypeWith<T> extends BlockType, With<T> {
        Function<String, T> withSupply();
    }

    interface Range extends BlockType { }
    interface Neutral extends BlockType {
        default String close() { return open(); }
    }
    interface NeutralWith<T> extends Neutral, BlockTypeWith<T> {
        Function<String, T> withSupply();
    }

    static Range range(String open, String close) {
        record RangeRecord(String open, String close) implements Range {}
        return new RangeRecord(open, close);
    }

    static Neutral neutral(String open) {
        record NeutralRecord(String open) implements Neutral {}
        return new NeutralRecord(open);
    }

    static <T> NeutralWith<T> neutral(String open, Function<String, T> withSupply) {
        record NeutralWithRecord<T>(String open, Function<String, T> withSupply) implements NeutralWith<T> {}
        return new NeutralWithRecord<>(open, withSupply);
    }

}
