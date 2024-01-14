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
package com.mammb.code.editor.syntax.markdown;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;

/**
 * Markdown.
 * @author Naotsugu Kobayashi
 */
public class Markdown {

    /** Java Token type. */
    public interface MdToken extends com.mammb.code.editor.syntax.base.TokenType {
        TokenType H1 = TokenType.build(Hue.BLUE);
        TokenType H2 = TokenType.build(Hue.BLUE);
        TokenType H3 = TokenType.build(Hue.BLUE);
        TokenType H4 = TokenType.build(Hue.BLUE);
        TokenType H5 = TokenType.build(Hue.BLUE);
        TokenType CODE = TokenType.build(Hue.CYAN);
        TokenType FENCE = TokenType.build();
    }

}
