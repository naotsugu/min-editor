/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.platform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The LockFile.
 * @author Naotsugu Kobayashi
 */
public class LockFile {

    private static final int OFFSET = 1;
    private final Path path;
    private FileChannel channel;
    private FileLock lock;

    /**
     * Creates a new {@code LockFile} instance that manages file-based locking functionality.
     *
     * @param path the path to the lock file to ensure single instance locking
     */
    public LockFile(Path path) {
        this.path = path;
    }

    /**
     * Attempts to acquire an exclusive lock on a file to ensure single-instance application behavior.
     * This method creates or opens the specified lock file, attempts to acquire a non-blocking lock,
     * writes the current process ID to the file, and sets up a shutdown hook to release the lock and
     * clean up the lock file upon application termination.
     * <ul>
     * - If the lock cannot be acquired, it attempts to activate the window of the process holding the lock by reading its PID
     *   from the file and invoking platform-specific window activation logic.
     * - If the lock is successfully acquired, the current process's PID is written to the file.
     * </ul>
     *
     * @throws IOException If an I/O error occurs while working with the lock file.
     * @throws IllegalStateException If another instance of the application is already running.
     */
    public void tryLock() throws Exception {

        channel = FileChannel.open(path,
            StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
        lock = channel.tryLock(0, OFFSET, false);
        if (lock == null) {
            Host.activateWindow(readPid());
            throw new IllegalStateException("another instance of the application is already running.");
        }
        writePid();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (lock != null) lock.close();
                if (channel != null) channel.close();
                Files.deleteIfExists(path);
            } catch (IOException ignore) {
            }
        }));
    }

    private void writePid() throws IOException {
        long pid = ProcessHandle.current().pid();
        channel.position(OFFSET);
        channel.write(ByteBuffer.wrap(String.valueOf(pid).getBytes()));
        channel.truncate(channel.position());
        channel.force(true);
    }

    private String readPid() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(64);
        channel.read(buf, OFFSET);
        buf.flip();
        return StandardCharsets.UTF_8.decode(buf).toString().trim();
    }

}
