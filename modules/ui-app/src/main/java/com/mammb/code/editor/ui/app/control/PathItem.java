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
package com.mammb.code.editor.ui.app.control;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * The path item.
 * @author Naotsugu Kobayashi
 */
public interface PathItem extends Path {

    Predicate<Path> exclude = p ->
        !".DS_Store".equals(p.getFileName().toString()) &&
        !"Thums.db".equals(p.getFileName().toString());

    /**
     * Get the name.
     * @return the name
     */
    String name();

    /**
     * Get the raw path.
     * @return the raw path
     */
    Path raw();


    @Override
    default int compareTo(Path other) {
        return Comparator.comparing((Path p) -> Files.isDirectory(p) ? -1 : 1)
            .thenComparing(p -> p)
            .compare(this.raw(), (other instanceof PathItem pi) ? pi.raw() : other);
    }

}
