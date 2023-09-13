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
package com.mammb.code.editor.syntax.basic;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;

/**
 * Diff.
 * @author Naotsugu Kobayashi
 */
public class Diff {

    /** Diff Token type. */
    public interface DiffToken extends com.mammb.code.editor.syntax.base.TokenType {
        TokenType ADD = TokenType.build(Hue.GREEN);
        TokenType DEL = TokenType.build(Hue.RED);
        TokenType SUM = TokenType.build(Hue.CYAN);
    }

}
