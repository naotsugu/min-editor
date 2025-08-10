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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;
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
        return path != null
            && java.nio.file.Files.isRegularFile(path)
            && java.nio.file.Files.isReadable(path);
    }

    static boolean isReadableDirectory(Path path) {
        return path != null
            && java.nio.file.Files.isDirectory(path)
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

    static Path createDirectories(Path dir, FileAttribute<?>... attrs) {
        try {
            return java.nio.file.Files.createDirectories(dir, attrs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Path createFile(Path file, FileAttribute<?>... attrs) {
        try {
            return java.nio.file.Files.createFile(file, attrs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> readAllLines(Path path) {
        try {
            return java.nio.file.Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> readAllLines(Path path, Charset cs) {
        try {
            return java.nio.file.Files.readAllLines(path, cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) {
        try {
            return java.nio.file.Files.write(path, lines, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void delete(Path path) {
        try {
            java.nio.file.Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean exists(Path path, LinkOption... options) {
        if (Objects.isNull(path)) return false;
        return java.nio.file.Files.exists(path, options);
    }

    static Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) {
        try {
            return java.nio.file.Files.createTempFile(dir, prefix, suffix, attrs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Path move(Path source, Path target, CopyOption... options) {
        try {
            return java.nio.file.Files.move(source, target, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static InputStream newInputStream(Path path, OpenOption... options) {
        try {
            return java.nio.file.Files.newInputStream(path, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    static void writeWith(Path path, Charset charset, Charset newCharset, String lineSeparator) {
        Path destinationPath = createTempFile(path.getParent(), path.getFileName().toString(), null);
        if (lineSeparator != null && !lineSeparator.isBlank()) {
            writeWith(path, charset, destinationPath, newCharset);
        } else {
            writeWith(path, charset, destinationPath, newCharset, lineSeparator);
        }
        move(destinationPath, path, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void writeWith(
            Path sourcePath, Charset sourceCharset,
            Path destinationPath, Charset destinationCharset) {
        try (Reader reader = java.nio.file.Files.newBufferedReader(sourcePath, sourceCharset);
             Writer writer = java.nio.file.Files.newBufferedWriter(destinationPath, destinationCharset)) {
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, charsRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeWith(
            Path sourcePath, Charset sourceCharset,
            Path destinationPath, Charset destinationCharset, String lineSeparator) {
        try (BufferedReader reader = java.nio.file.Files.newBufferedReader(sourcePath, sourceCharset);
             Writer writer = java.nio.file.Files.newBufferedWriter(destinationPath, destinationCharset)) {
            reader.lines().forEachOrdered(line -> {
                try {
                    writer.write(line);
                    writer.write(lineSeparator);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
