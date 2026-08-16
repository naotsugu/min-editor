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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * The DomainSocket.
 * @author Naotsugu Kobayashi
 */
public class DomainSocket implements AutoCloseable {

    /** The logger. */
    private static final System.Logger log = System.getLogger(DomainSocket.class.getName());

    private final Path socketPath;
    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private ServerSocketChannel channel;
    private ExecutorService executor;

    public DomainSocket(Path socketPath) {
        this.socketPath = Objects.requireNonNull(socketPath);
    }

    public void addListener(Consumer<String> listener) {
        this.listeners.add(listener);
    }

    public synchronized void start() throws Exception {

        if (isRunning.get()) {
            return;
        }

        deleteIfExists();

        this.channel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
        this.channel.bind(UnixDomainSocketAddress.of(socketPath));

        this.isRunning.set(true);
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "uds-server-thread");
            t.setDaemon(true);
            return t;
        });
        this.executor.submit(this::listenLoop);
    }

    private void listenLoop() {
        while (isRunning.get() && channel != null && channel.isOpen()) {
            try {
                SocketChannel clientChannel = channel.accept();

                try (clientChannel;
                     BufferedReader reader = new BufferedReader(
                         new InputStreamReader(Channels.newInputStream(clientChannel)))) {

                    String message;
                    while ((message = reader.readLine()) != null) {
                        notifyListeners(message);
                    }
                }
            } catch (Exception e) {
                if (isRunning.get()) {
                    log.log(System.Logger.Level.WARNING, "error accepting client connection", e);
                }
            }
        }
    }

    private void notifyListeners(String message) {
        try {
            listeners.forEach(l -> l.accept(message));
        } catch (Exception e) {
            log.log(System.Logger.Level.WARNING, "error accepting client connection", e);
        }
    }

    @Override
    public synchronized void close() {

        if (!isRunning.compareAndSet(true, false)) {
            return;
        }

        try {
            if (executor != null) executor.shutdownNow();
            if (channel != null) channel.close();
        } catch (Exception e) {
            log.log(System.Logger.Level.WARNING, "error closing channel", e);
        }

        deleteIfExists();
    }

    private void deleteIfExists() {
        try {
            Files.deleteIfExists(socketPath);
        } catch (Exception e) {
            log.log(System.Logger.Level.ERROR, "could not delete socket at " + socketPath, e);
        }
    }

}
