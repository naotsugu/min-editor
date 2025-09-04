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
package com.mammb.code.editor.core.tools;

import com.mammb.code.editor.core.Files;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * The source bytes 16.
 * @author Naotsugu Kobayashi
 */
public class Source16 implements Source<byte[]> {

    /** The file channel. */
    private final FileChannel fc;
    /** The length of the file. */
    private final long length;
    /** The source name. */
    private final String name;
    /** The buffer. */
    private final ByteBuffer bb = ByteBuffer.allocate(16);

    /**
     * Initializes a Source16 instance with the given file path.
     * @param path the path to the file to be read; must not be null and should point to a readable file
     */
    public Source16(Path path) {
        fc = Files.newFileChannel(path);
        length = Files.size(fc);
        name = path.getFileName().toString();
    }

    @Override
    public byte[] get(int index) {
        try {
            bb.clear();
            fc.position(index * 16L);
            fc.read(bb);
            bb.flip();
            return Arrays.copyOf(bb.array(), bb.limit());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return Math.toIntExact(length / 16 + (length % 16 == 0 ? 0 : 1));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void close() {
        try {
            fc.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
