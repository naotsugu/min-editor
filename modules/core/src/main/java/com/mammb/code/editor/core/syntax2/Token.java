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

import com.mammb.code.editor.core.syntax2.TokenType.*;
import java.util.Objects;

/**
 * The syntax token.
 * @author Naotsugu Kobayashi
 */
public interface Token {

    TokenType type();

    String value();

    default boolean isRangeBlockStart() {
        return switch (type()) {
            case RangeBlock type -> Objects.equals(type.open(), value());
            default -> false;
        };
    }

    default boolean isRangeBlockEnd() {
        return switch (type()) {
            case RangeBlock type -> Objects.equals(type.close(), value());
            default -> false;
        };
    }

    default boolean isNeutralBlock() {
        return switch (type()) {
            case NeutralBlock type -> true;
            default -> false;
        };
    }

    interface AttributedToken<R> extends Token, TokenType.Attributed<R> {
        R attr();
    }



}
