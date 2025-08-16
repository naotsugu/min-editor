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

/**
 * Represents a source of elements with a specific size and an optional name.
 * This interface is typically used to abstract collections or resources
 * that allow indexed retrieval of elements.
 * @param <T> the type of elements provided by this source
 * @author Naotsugu Kobayashi
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

    /**
     * Create a new {@link Source} with the specified list and name.
     * @param list the specified list
     * @param name the specified name
     * @return a new {@link Source} with the specified list and name
     * @param <T> the type of elements provided by this source
     */
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

    /**
     * Create a new {@link Source} with the specified list and empty name.
     * @param list the specified list
     * @return a new {@link Source} with the specified list and empty name
     * @param <T> the type of elements provided by this source
     */
    static <T> Source<T> of(List<T> list) {
        return of(list, "");
    }

    /**
     * Create a new {@link Source} with the specified path and charset.
     * @param path the specified path
     * @param cs the specified charset
     * @return a new {@link Source} with the specified path and charset
     */
    static Source<String> of(Path path, Charset cs) {
        return Source.of(Files.readStrictAllLines(path, cs), path.toString());
    }

    /**
     * Create a new {@link Source} with the specified path, charset and name.
     * @param path the specified path
     * @param cs the specified charset
     * @param name the specified name
     * @return a new {@link Source} with the specified path, charset and name
     */
    static Source<String> of(Path path, Charset cs, String name) {
        return Source.of(Files.readStrictAllLines(path, cs), name);
    }

}
