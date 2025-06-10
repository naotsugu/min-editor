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

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Query;
import java.nio.file.Path;

/**
 * The ContentName.
 * @author Naotsugu Kobayashi
 */
public class ContentName implements Name {

    private final Content content;

    private final String name;

    private ContentName(Content content, String name) {
        this.content = content;
        this.name = (name == null || name.isBlank()) ? "Untitled" : name;
    }

    public static ContentName of(Content content, String name) {
        return new ContentName(content, name);
    }

    public static ContentName of(Content content) {
        return new ContentName(content, null);
    }

    @Override
    public String canonical() {
        return content.path().map(Path::toAbsolutePath).map(Path::toString).orElse(name);
    }

    @Override
    public String plain() {
        return content.path().map(Path::getFileName).map(Path::toString).orElse(name);
    }

    @Override
    public String contextual() {
        return content.query(Query.modified) ? "*" + plain() : plain();
    }

    public Name mute() {
        record NameRecord(String canonical, String plain, String contextual) implements Name { }
        return new NameRecord(canonical(), plain(), contextual());
    }

}
