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
package com.mammb.code.editor.model.until;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.until.impl.TraverseBackward;
import com.mammb.code.editor.model.until.impl.TraverseForward;
import com.mammb.code.editor.model.until.impl.TraverseStack;

import java.util.function.Consumer;

/**
 * Traverse.
 * @author Naotsugu Kobayashi
 */
public interface Traverse extends Consumer<byte[]> {

    /**
     * Performs this operation on the given argument.
     * @param t the input argument
     */
    void accept(byte[] t);

    /**
     * Get the result of traverse as OffsetPoint
     * @return the offset point
     */
    OffsetPoint asOffsetPoint();

    /**
     * Create a forward traverse.
     * @param base the base offset point
     * @return the traverse
     */
    static Traverse forwardOf(OffsetPoint base) {
        return TraverseForward.of(base);
    }

    /**
     * Create a backward traverse.
     * @param base the base offset point
     * @return the traverse
     */
    static Traverse backwardOf(OffsetPoint base) {
        return TraverseBackward.of(base);
    }

    /**
     * Create a stack traverse.
     * @param base the base offset point
     * @return the traverse
     */
    static Traverse stackOf(OffsetPoint base) {
        return TraverseStack.of(base);
    }

}
