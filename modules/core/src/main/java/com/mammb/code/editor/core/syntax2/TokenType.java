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
package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.syntax.Syntax;
import java.util.function.Function;

/**
 * The syntax token type.
 * @author Naotsugu Kobayashi
 */
public interface TokenType {

    interface Block extends TokenType { }

    interface RangeBlock extends Block {
        String open();
        String close();
    }

    interface NeutralBlock extends Block {
        String open();
    }

    interface Attributed<R> { }

    interface AttributedNeutralBlock<T, R>
            extends NeutralBlock, Attributed<R> {
        Function<T, R> rule();
    }

    static RangeBlock of(String open, String close) {
        record RangeBlockRecord(String open, String close)
            implements RangeBlock {}
        return new RangeBlockRecord(open, close);
    }

    static NeutralBlock of(String open) {
        record NeutralBlockRecord(String open) implements NeutralBlock {}
        return new NeutralBlockRecord(open);
    }

    static AttributedNeutralBlock<String, Syntax> of(String open, Function<String, Syntax> rule) {
        record FenceBlockRecord(String open, Function<String, Syntax> rule)
            implements AttributedNeutralBlock<String, Syntax>, Attributed<Syntax> { }
        return new FenceBlockRecord(open, rule);
    }

}
