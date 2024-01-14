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
package com.mammb.code.editor.syntax.javascript;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;

/**
 * Json.
 * @author Naotsugu Kobayashi
 */
public class Json {

    /** JSON Token type. */
    public interface JsonToken extends com.mammb.code.editor.syntax.base.TokenType {

        TokenType KEY = TokenType.build(Hue.DEEP_ORANGE);
        TokenType TEXT = TokenType.build(Hue.GREEN);
        TokenType NUMBER = TokenType.build(Hue.CYAN);
        TokenType LITERAL  = TokenType.build(Hue.CYAN);
        TokenType LINE_COMMENT = TokenType.build(Hue.DARK_GREY);
        TokenType COMMENT = TokenType.build(Hue.GREEN);

    }

}
