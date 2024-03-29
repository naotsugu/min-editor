/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.mammb.code.editor.model.until;

import com.mammb.code.editor.model.until.impl.UntilCharLen;
import com.mammb.code.editor.model.until.impl.UntilLf;
import com.mammb.code.editor.model.until.impl.UntilLfInclusive;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Until.
 * @author Naotsugu Kobayashi
 */
public interface Until<T> extends Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate
     */
    boolean test(T t);


    /**
     * Composite the specified consumer.
     * @param consumer the specified consumer
     * @return the Until after composited
     */
    default Until<T> with(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return t -> {
            boolean closed = test(t);
            consumer.accept(t);
            return closed;
        };
    }


    /**
     * Composite the specified consumer.
     * @param consumer the specified consumer
     * @return the Until after composited
     */
    default Until<T> withLess(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return t -> {
            boolean closed = test(t);
            if (!closed) {
                consumer.accept(t);
            }
            return closed;
        };
    }


    /**
     * Create a new Until char length.
     * @return the Until char length
     */
    static Until<byte[]> charLen(int length) {
        return new UntilCharLen(length);
    }


    /**
     * Create a new Until LF.
     * @param count the line feed count
     * @return the Until LF predicate
     */
    static Until<byte[]> lf(int count) {
        return new UntilLf(count);
    }


    /**
     * Create a new Until LF.
     * @return the Until LF predicate
     */
    static Until<byte[]> lfInclusive() {
        return new UntilLfInclusive(1);
    }


    /**
     * Create a new Until LF.
     * @return the Until LF predicate
     */
    static Until<byte[]> lfInclusive(int count) {
        return new UntilLfInclusive(count);
    }

}
