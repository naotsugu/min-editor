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
package com.mammb.code.editor.model.find.impl;

import com.mammb.code.editor.model.find.FindSpec;
import com.mammb.code.editor.model.find.Match;
import java.util.Arrays;
import java.util.Objects;

/**
 * The find specification.
 * @author Naotsugu Kobayashi
 */
public class FindSpecImpl implements FindSpec {

    /** The empty match array. */
    private final Match[] empty = new Match[0];
    /** The string to be searched. */
    private final String cs;
    /** Whether it is a one-shot find. */
    private final boolean once;
    /** Whether it is a forward find. */
    private final boolean forward;
    /** The result buffer. */
    private Match[] buffer = new Match[3];


    /**
     * Constructor.
     * @param cs the string to be searched
     * @param once whether it is a one-shot find
     * @param forward whether it is a forward find
     */
    public FindSpecImpl(String cs, boolean once, boolean forward) {
        this.cs = Objects.requireNonNull(cs);
        this.once = once;
        this.forward = forward;
    }


    @Override
    public Match[] match(String text) {

        if (text == null || text.isEmpty() || cs.isEmpty()) {
            return empty;
        }

        int foundCount = 0;
        for (int i = 0; i < text.length();) {
            int ret = text.indexOf(cs, i);
            if (ret < 0) {
                break;
            }
            if (buffer.length <= foundCount) {
                buffer = grow(foundCount + 1);
            }
            buffer[foundCount++] = Match.of(ret, cs.length(), text.codePointCount(0, ret));
            i = ret + cs.length();
        }
        return (foundCount == 0) ? empty : Arrays.copyOf(buffer, foundCount);
    }


    @Override
    public boolean isEmpty() {
        return cs.isEmpty();
    }

    /**
     * Grow this array.
     * @param minCapacity the growth capacity
     * @return the grown array
     */
    private Match[] grow(int minCapacity) {
        int oldCapacity = buffer.length;
        int newCapacity = Math.clamp(oldCapacity >> 1, minCapacity, Integer.MAX_VALUE - 8);
        return buffer = Arrays.copyOf(buffer, newCapacity);
    }

    @Override
    public boolean once() {
        return once;
    }

    @Override
    public boolean forward() {
        return forward;
    }

}
