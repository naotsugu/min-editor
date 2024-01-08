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
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * The basic path item.
 * @author Naotsugu Kobayashi
 */
public class BasicPath implements PathItem {

    /** The raw path. */
    private final Path raw;

    /**
     * Constructor.
     * @param raw the row path
     */
    public BasicPath(Path raw) {
        this.raw = raw;
    }

    @Override
    public String name() {
        return raw.getFileName().toString();
    }

    @Override
    public Path raw() {
        return raw;
    }

    @Override
    public FileSystem getFileSystem() {
        return raw.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return raw.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return raw.getRoot();
    }

    @Override
    public Path getFileName() {
        return raw.getFileName();
    }

    @Override
    public Path getParent() {
        return raw.getParent();
    }

    @Override
    public int getNameCount() {
        return raw.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return raw.getName(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return raw.subpath(beginIndex, endIndex);
    }

    @Override
    public boolean startsWith(Path other) {
        return raw.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return raw.endsWith(other);
    }

    @Override
    public Path normalize() {
        return raw.normalize();
    }

    @Override
    public Path resolve(Path other) {
        return raw.resolve(other);
    }

    @Override
    public Path relativize(Path other) {
        return raw.relativize(other);
    }

    @Override
    public URI toUri() {
        return raw.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return raw.toAbsolutePath();
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return raw.toRealPath(options);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return raw.register(watcher, events, modifiers);
    }

    @Override
    public boolean equals(Object other) {
        return raw.equals(other);
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    @Override
    public String toString() {
        return raw.toString();
    }

}
