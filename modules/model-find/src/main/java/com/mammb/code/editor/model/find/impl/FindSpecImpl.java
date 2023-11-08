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
    private final boolean oneshot;
    /** The result buffer. */
    private Match[] buffer = new Match[3];


    /**
     * Constructor.
     * @param cs the string to be searched
     */
    public FindSpecImpl(String cs) {
        this.cs = Objects.requireNonNull(cs);
        this.oneshot = false;
    }


    @Override
    public Match[] match(String text) {

        if (text == null || text.isEmpty() || cs.isEmpty()) {
            return empty;
        }

        int findCount = 0;
        for (int i = 0; i < text.length();) {
            int ret = text.indexOf(cs, i);
            if (ret < 0) {
                break;
            }
            if (buffer.length <= findCount) {
                buffer = grow(findCount + 1);
            }
            buffer[findCount++] = Match.of(ret, cs.length());
            i = ret + cs.length();
        }
        return (findCount == 0) ? empty : Arrays.copyOf(buffer, findCount);
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

    /**
     * Get whether it is a one-shot find.
     * @return {@code true} if it is a one-shot find
     */
    @Override
    public boolean oneshot() {
        return oneshot;
    }

}