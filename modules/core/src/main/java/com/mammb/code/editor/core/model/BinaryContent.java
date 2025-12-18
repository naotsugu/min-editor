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
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.tools.BinaryView;
import com.mammb.code.editor.core.tools.Source;
import com.mammb.code.editor.core.tools.Source16;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

/**
 * The BinaryContent.
 * @author Naotsugu Kobayashi
 */
public class BinaryContent extends ContentAdapter {

    /** The pear content. */
    private final Content pear;

    /** The binary source. */
    private Path source;

    /**
     * Constructs a new BinaryContent instance.
     * @param source the binary source path
     * @param pear the content representation for editing
     */
    private BinaryContent(Path source, Content pear) {
        this.pear = pear;
        this.source = source;
    }

    /**
     * Creates a new instance of BinaryContent by processing the provided source path.
     * A temporary file is generated with a transformed view of the source content,
     * and a representation for editing is created to associate with the binary content.
     *
     * @param source the binary source path which is used to create the binary content instance
     * @return a new BinaryContent instance based on the given source path
     */
    public static BinaryContent of(Path source) {
        Path temp = Files.createTempFile(null, source.getFileName().toString(), "");
        temp.toFile().deleteOnExit();
        Path view = Files.write(temp, BinaryView.run(new Source16(source)));
        Content pear = new TextEditContent(view);
        return new BinaryContent(source, pear);
    }

    @Override
    public Optional<Path> path() {
        return Optional.of(source);
    }

    @Override
    public Optional<FileTime> lastModifiedTime() {
        return Optional.ofNullable(Files.lastModifiedTime(source));
    }

    @Override
    public void save(Path path) {
        BinaryView.save(path, source(pear));
        source = path;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.ModelName _ ->
                (R) Name.of(path().orElse(null), pear.query(Query.modified));
            default -> super.query(query);
        };
    }

    @Override
    protected Content pear() {
        return pear;
    }

    private Source<String> source(Content content) {
        return new Source<>() {
            @Override public String get(int index) { return content.getText(index); }
            @Override public int size() { return content.rows(); }
            @Override public String name() { return source.getFileName().toString(); }
        };
    }

}
