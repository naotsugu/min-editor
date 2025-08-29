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
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.tools.Diff;
import com.mammb.code.editor.core.tools.Source;
import com.mammb.code.editor.core.tools.SourcePair;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * The diff run.
 * @author Naotsugu Kobayashi
 */
public class DiffRun {

    /** The original source and the revised source. */
    private final Source<String> org;

    /** The original source and the revised source. */
    private final Source<String> rev;

    /**
     * Constructor.
     * @param org the original source
     * @param rev the revised source
     */
    private DiffRun(Source<String> org, Source<String> rev) {
        this.org = org;
        this.rev = rev;
    }

    /**
     * Create a new {@link DiffRun} with the specified content.
     * @param content the specified content
     * @return a new {@link DiffRun} with the specified content
     */
    public static DiffRun of(Content content) {
        return new DiffRun(saved(content), current(content));
    }

    /**
     * Create a new {@link DiffRun} with the specified content and other content.
     * @param content the specified content
     * @param other the specified other content
     * @return a new {@link DiffRun} with the specified content and other content
     */
    public static DiffRun of(Content content, Path other) {
        return new DiffRun(current(content), Source.of(other, content.query(Query.charCode)));
    }

    /**
     * Write the diff run to the specified path.
     * @param path the specified path
     * @return the specified path
     */
    public Path write(Path path) {
        Files.write(path, Diff.run(new SourcePair<>(org, rev)).asUnifiedFormText(3), StandardCharsets.UTF_8, "\n");
        return path;
    }

    /**
     * Write the diff run to the specified path.
     * @param path the specified path
     * @return the specified path
     */
    public Path writeWithoutFold(Path path) {
        Files.write(path, Diff.run(new SourcePair<>(org, rev)).asUnifyTexts(), StandardCharsets.UTF_8, "\n");
        return path;
    }

    private static Source<String> saved(Content content) {
        if (content.path().isEmpty()) {
            return current(content);
        } else {
            return Source.of(content.path().get(), content.query(Query.charCode));
        }
    }

    private static Source<String> current(Content content) {
        return new Source<>() {
            @Override public String get(int index) { return content.getText(index).replaceAll("\\R", ""); }
            @Override public int size() { return content.rows(); }
            @Override public String name() { return content.path().isEmpty() ? "current" : content.path().get().toString(); }
        };
    }

}
