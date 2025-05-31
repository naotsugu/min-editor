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
package com.mammb.code.editor.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.stream.Stream;

/**
 * The utility's of file system.
 * @author Naotsugu Kobayashi
 */
public interface Files {

    static long size(Path path) {
        try {
            return java.nio.file.Files.size(path);
        } catch (Exception ignore) { }
        return 0;
    }

    static byte[] readAllBytes(Path path) {
        try {
            return java.nio.file.Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isReadableFile(Path path) {
        return java.nio.file.Files.isRegularFile(path)
            && java.nio.file.Files.isReadable(path);
    }

    static boolean isReadableDirectory(Path path) {
        return java.nio.file.Files.isDirectory(path)
            && java.nio.file.Files.isReadable(path);
    }

    static Stream<Path> list(Path path) {
        try {
            return java.nio.file.Files.list(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> listAbsolutePath(Path path) {
        try (var stream = list(path)) {
            return stream.map(Path::toAbsolutePath)
                .map(Path::toString).toList();
        }
    }

    static FileTime lastModifiedTime(Path path) {
        try {
            return java.nio.file.Files.getLastModifiedTime(path);
        } catch (IOException ignore) { }
        return null;
    }

}
