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
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.Optional;

/**
 * The named Content.
 * @author Naotsugu Kobayashi
 */
public class NamedContent extends ContentAdapter {

    /** The pear content. */
    private Content pear;

    /** The interim name. */
    private String name;

    /** Whether it has been synced or not. */
    private boolean synced = false;


    private NamedContent(Content pear, String name) {
        this.pear = Objects.requireNonNull(pear);
        this.name = Objects.requireNonNull(name);
    }

    public static Content interimOf(Path path, String name) {
        Content content = new TextEditContent(path);
        return new NamedContent(content, name);
    }

    public NamedContent asName(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public Optional<Path> path() {
        return synced ? pear.path() : Optional.empty();
    }

    @Override
    public Optional<FileTime> lastModifiedTime() {
        return synced ? pear.lastModifiedTime() : Optional.empty();
    }

    @Override
    public void save(Path path) {
        pear.save(path);
        name = path.getFileName().toString();
        synced = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.ModelName _ ->
                (R) Name.of(synced ? pear.path().orElse(null) : null, pear.query(Query.modified), name);
            default -> super.query(query);
        };
    }

    @Override
    protected Content pear() {
        return pear;
    }

}
