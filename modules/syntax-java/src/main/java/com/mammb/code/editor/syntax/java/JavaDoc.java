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
package com.mammb.code.editor.syntax.java;

import com.mammb.code.editor.syntax.base.Hue;
import com.mammb.code.editor.syntax.base.TokenType;
import com.mammb.code.editor.syntax.base.Trie;

import java.util.stream.Stream;

/**
 * JavaDoc.
 * @author Naotsugu Kobayashi
 */
public class JavaDoc {

    /** The name. */
    public static final String name = "javadoc@java";


    /** Java Token type. */
    public interface JavaDocToken extends com.mammb.code.editor.syntax.base.TokenType {
        TokenType TAG = TokenType.build(Hue.CYAN);
    }


    /**
     * Get the block tags trie.
     * @return the keyword trie
     */
    public static Trie blockTags() {

        Trie trie = Trie.of();

        Stream.of("""
        @author,@deprecated,@exception,@hidden,@param,@provides,@return,@see,@serial,
        @serialData,@serialField,@since,@throws,@uses,@version"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }


    /**
     * Get the Inline tags trie(like `{@code }`).
     * @return the keyword trie
     */
    public static Trie inlineTags() {

        Trie trie = Trie.of();

        Stream.of("""
        @code,@docRoot,@index,@inheritDoc,@link,@linkplain,@literal,@summary,@systemProperty,@value"""
            .split("[,\\s]")).forEach(trie::put);

        return trie;
    }

}
