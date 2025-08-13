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
public record SourceSet<T>(Source<T> org, Source<T> rev) {

    public boolean equalsAt(int indexOrg, int indexRev) {
        return Objects.equals(org.get(indexOrg), rev.get(indexRev));
    }

    public interface Source<T> {

        T get(int index);

        int size();

        static <T> Source<T> of(List<T> list) {
            return new Source<>() {
                @Override
                public T get(int index) {
                    return list.get(index);
                }
                @Override
                public int size() {
                    return list.size();
                }
            };
        }

        static Source<String> of(Path path, Charset cs) {
            return Source.of(Files.readAllLines(path, cs));
        }

    }

}
