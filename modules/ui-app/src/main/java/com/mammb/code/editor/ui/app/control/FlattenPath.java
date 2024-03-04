/*
 * Copyright 2023-2024 the original author or authors.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * The flatten path.
 * @author Naotsugu Kobayashi
 */
public class FlattenPath extends BasicPath {

    /** logger. */
    private static final System.Logger log = System.getLogger(FlattenPath.class.getName());

    /** The limit of path depth. */
    private static final int limitOfDepth = 10;

    /** The flatten path. */
    private final Path flatten;


    /**
     * Constructor.
     * @param raw the raw path
     */
    public FlattenPath(Path raw) {
        super(raw);
        this.flatten = delve(raw);
    }


    /**
     * Create the path item.
     * @param raw the raw path
     * @return the path item
     */
    public static PathItem of(Path raw) {
        return new FlattenPath(raw);
    }


    /**
     * Delve paths that have only one file in the folder hierarchy.
     * @param path the path
     * @return delved paths
     */
    private static Path delve(Path path) {
        Path p = path;
        for (int i = 0; i < limitOfDepth; i++) {
            if (Files.isDirectory(p) && Files.isReadable(p)) {
                try (Stream<Path> stream = Files.list(p).filter(PathItem.exclude)) {
                    var list = stream.toList();
                    if (list.size() == 1) {
                        p = list.getFirst();
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    log.log(System.Logger.Level.WARNING, e);
                    break;
                }
            } else {
                break;
            }
        }
        return p;
    }


    @Override
    public String name() {
        if (super.raw().equals(flatten)) {
            return super.name();
        } else {
            if (getParent() == null) {
                return flatten.getFileName().toString();
            } else {
                return getParent().relativize(flatten).toString();
            }
        }
    }


    @Override
    public Path raw() {
        return flatten;
    }

}
