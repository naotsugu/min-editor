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
package com.mammb.code.editor.syntax.base;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Token.
 * @author Naotsugu Kobayashi
 */
public interface TokenType extends Comparable<TokenType> {

    /** The number of serial. */
    AtomicInteger serial = new AtomicInteger(0);

    /** The type of empty. */
    TokenType EMPTY = build();
    /** The type of any. */
    TokenType ANY = build();
    /** The type of whitespace. */
    TokenType SP = build();
    /** The type of eol. */
    TokenType EOL = build();

    /** The hue. */
    Hue hue();

    /** The serial(Used as token priority). */
    int serial();


    static TokenType build() {
        return build(Hue.NONE);
    }


    static TokenType build(Hue hue) {
        record TokenTypeRecord(int serial, Hue hue) implements TokenType { }
        return new TokenTypeRecord(serial.getAndIncrement(), hue);
    }

    @Override
    default int compareTo(TokenType o) {
        return Integer.compare(serial(), o.serial());
    }

}
