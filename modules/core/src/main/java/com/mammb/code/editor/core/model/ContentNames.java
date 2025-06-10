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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Name;
import java.nio.file.Path;

/**
 * The NameRecord.
 * @author Naotsugu Kobayashi
 */
public class ContentNames {

    public static Name of(TextEditContent content, String name) {
        String n = (name == null || name.isBlank()) ? "Untitled" : name;
        return new Name() {
            @Override
            public String canonical() {
                return content.path().map(Path::toAbsolutePath).map(Path::toString).orElse(n);
            }
            @Override
            public String plain() {
                return content.path().map(Path::getFileName).map(Path::toString).orElse(n);
            }
            @Override
            public String contextual() {
                return (content.isModified() ? "*" : "") + plain();
            }
        };
    }

    public static Name of(TextEditContent content) {
        return of(content, null);
    }

    public static Name readonlyOf(Name name) {
        return new Name() {
            @Override
            public String canonical() {
                return name.canonical();
            }
            @Override
            public String plain() {
                return name.plain();
            }
            @Override
            public String contextual() {
                return "[" + plain() + "]";
            }
        };
    }

}
