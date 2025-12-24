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
    private final Content pear;

    /** The interim name. */
    private String name;

    /** interim?. */
    private final boolean interim;

    /** Whether it has been synced or not. */
    private boolean synced = false;


    /**
     * Constructor.
     */
    private NamedContent(Content pear, String name, boolean interim) {
        this.pear = Objects.requireNonNull(pear);
        this.name = Objects.requireNonNull(name);
        this.interim = interim;
    }

    /**
     * Create a named content with an interim name.
     * @param path the path
     * @param name the name
     * @return {@link NamedContent}
     */
    public static Content interimOf(Path path, String name) {
        Content content = new TextEditContent(path);
        return new NamedContent(content, name, true);
    }

    /**
     * Create a named content.
     * @param content the pear content
     * @param name the name
     * @return {@link NamedContent}
     */
    public static Content of(Content content, String name) {
        return new NamedContent(content, name, false);
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
        synced = true;
        if (interim) {
            name = path.getFileName().toString();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.Modified _ when !synced ->
                (R) Boolean.TRUE;
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
