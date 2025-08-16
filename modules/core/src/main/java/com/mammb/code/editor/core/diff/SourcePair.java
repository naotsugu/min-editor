/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.diff;

import com.mammb.code.editor.core.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * The diff source set.
 * @author Naotsugu Kobayashi
 */
public record SourcePair<T>(Source<T> org, Source<T> rev) {

    /**
     * Check if the specified index elements are equal.
     * @param indexOrg the specified index of original
     * @param indexRev the specified index of revise
     * @return {@code true} if the specified index elements are equal
     */
    public boolean equalsAt(int indexOrg, int indexRev) {
        return Objects.equals(org.get(indexOrg), rev.get(indexRev));
    }

    /**
     * Get the maximum size of this source pair.
     * @return the maximum size of this source pair
     */
    public int sizeMax() {
        return Math.max(org.size(), rev.size());
    }

    /**
     * Check if this source pair has named.
     * @return {@code true} if this source pair has named
     */
    public boolean named() {
        return !org.name().isEmpty() || !rev.name().isEmpty();
    }

    /**
     * Represents a source of elements with a specific size and an optional name.
     * This interface is typically used to abstract collections or resources
     * that allow indexed retrieval of elements.
     * @param <T> the type of elements provided by this source
     */
    public interface Source<T> {

        /**
         * Get the element at the specified index.
         * @param index the specified index
         * @return the element at the specified index
         */
        T get(int index);

        /**
         * Get the size of this source.
         * @return the size of this source
         */
        int size();

        /**
         * Get the name of this source.
         * @return the name of this source
         */
        String name();

        static <T> Source<T> of(List<T> list, String name) {
            return new Source<>() {
                @Override
                public T get(int index) {
                    return list.get(index);
                }
                @Override
                public int size() {
                    return list.size();
                }
                @Override
                public String name() {
                    return name;
                }
            };
        }

        static <T> Source<T> of(List<T> list) {
            return of(list, "");
        }

        static Source<String> of(Path path, Charset cs) {
            return Source.of(Files.readStrictAllLines(path, cs));
        }

    }

}
