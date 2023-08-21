/*
 * Copyright 2019-2023 the original author or authors.
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

import com.mammb.code.editor2.syntax.Hue;
import com.mammb.code.editor2.syntax.TokenType;
import com.mammb.code.editor2.syntax.Trie;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Markdown.
 * @author Naotsugu Kobayashi
 */
public class Markdown {

    /** Java Token type. */
    public interface ToKenType extends com.mammb.code.editor2.syntax.TokenType {
        TokenType COMMENT = TokenType.build(Hue.GREEN);
    }

}
