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

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * The flatten path.
 * @author Naotsugu Kobayashi
 */
public class FlattenPath implements Path {

    private final Path raw;

    private final Path flatten;

    private static final int limitOfCharLength = 64;


    public FlattenPath(Path raw) {
        this.raw = raw;
        this.flatten = delve(raw);
    }


    private static Path delve(Path path) {
        for (var p = path; ;) {
            if (Files.isDirectory(p) && Files.isReadable(p)) {
                try (Stream<Path> stream = Files.list(p)) {
                    var list = stream.toList();
                    if (list.size() == 1) {
                        p = list.getFirst();
                        if (p.toString().length() < limitOfCharLength) {
                            continue;
                        }
                    }
                } catch (IOException e) {
                    return p;
                }
            }
            return p;
        }
    }


    public String getFlattenName() {
        return raw.relativize(flatten).toString();
    }


    @Override
    public FileSystem getFileSystem() {
        return flatten.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return flatten.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return flatten.getRoot();
    }

    @Override
    public Path getFileName() {
        return flatten.getFileName();
    }

    @Override
    public Path getParent() {
        return flatten.getParent();
    }

    @Override
    public int getNameCount() {
        return flatten.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return flatten.getName(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return flatten.subpath(beginIndex, endIndex);
    }

    @Override
    public boolean startsWith(Path other) {
        return flatten.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return flatten.endsWith(other);
    }

    @Override
    public Path normalize() {
        return flatten.normalize();
    }

    @Override
    public Path resolve(Path other) {
        return flatten.resolve(other);
    }

    @Override
    public Path relativize(Path other) {
        return flatten.relativize(other);
    }

    @Override
    public URI toUri() {
        return flatten.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return flatten.toAbsolutePath();
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return flatten.toRealPath(options);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return flatten.register(watcher, events, modifiers);
    }

    @Override
    public int compareTo(Path other) {
        return flatten.compareTo(other);
    }

    @Override
    public boolean equals(Object other) {
        return flatten.equals(other);
    }

    @Override
    public int hashCode() {
        return flatten.hashCode();
    }

    @Override
    public String toString() {
        return flatten.toString();
    }

}
