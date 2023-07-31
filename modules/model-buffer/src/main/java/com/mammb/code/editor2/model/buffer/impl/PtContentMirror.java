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
package com.mammb.code.editor2.model.buffer.impl;

import com.mammb.code.editor2.model.buffer.Content;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.piecetable.buffer.Charsets;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * PtContentMirror.
 * @author Naotsugu Kobayashi
 */
public class PtContentMirror implements Content {

    /** The original path. */
    private Path original;

    /** The charset of the original content. */
    private Charset originalCs;

    /** The copied path. */
    private Path mirror;

    private PtContent peer;


    public PtContentMirror(Path original, Charset charset) {
        this.original = original;
        this.originalCs = charset;
        this.mirror = createMirror(original);
        copy(original, originalCs, mirror, StandardCharsets.UTF_8);
        this.peer = new PtContent(mirror);
    }


    public static Content of(Path path) {
        if (path == null) return new PtContent();
        final Charset cs = detectCharset(path);
        return cs.equals(StandardCharsets.UTF_8)
            ? new PtContent(path)
            : new PtContentMirror(path, cs);
    }


    @Override
    public byte[] bytes(int cpOffset, Predicate<byte[]> until) {
        return peer.bytes(cpOffset, until);
    }

    @Override
    public byte[] bytesBefore(int cpOffset, Predicate<byte[]> until) {
        return peer.bytesBefore(cpOffset, until);
    }

    @Override
    public void insert(OffsetPoint point, CharSequence cs) {
        peer.insert(point, cs);
    }

    @Override
    public void delete(OffsetPoint point, int len) {
        peer.delete(point, len);
    }

    @Override
    public void save() {
        peer.save();
        copy(mirror, StandardCharsets.UTF_8, original, originalCs);
    }

    @Override
    public void saveAs(Path path) {
        peer.save();
        original = path;
        copy(mirror, StandardCharsets.UTF_8, original, originalCs);
    }

    @Override
    public Path path() {
        return original;
    }


    private static Charset detectCharset(Path path) {
        try (var is = Files.newInputStream(path)) {
            return Charsets.charsetOf(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static Path createMirror(Path original) {
        try {
            final String fileName = original.getFileName().toString();
            final int extensionPos = fileName.lastIndexOf('.');
            final String name = String.format("%-3s", fileName.substring(0, extensionPos)).replace(' ', '_');
            final String extension = fileName.substring(extensionPos);
            Path tempFile = Files.createTempFile(name, extension);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void copy(Path source, Charset sourceCs, Path dest, Charset destCs) {
        try (BufferedReader r = Files.newBufferedReader(source, sourceCs);
             BufferedWriter w = Files.newBufferedWriter(dest, destCs)) {
            char[] buffer = new char[8192];
            int read;
            while ((read = r.read(buffer)) != -1)
                w.write(buffer, 0, read);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
