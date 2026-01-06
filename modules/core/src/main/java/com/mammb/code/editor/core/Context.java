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
package com.mammb.code.editor.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * The context.
 * @author Naotsugu Kobayashi
 */
public interface Context {

    /** The platform (os) name. */
    String platform = platform();

    /**
     * Get the config.
     * @return the config
     */
    Config config();

    void opened(Path path);

    void closed(Path path);

    /**
     * Push the recent path.
     * @param path the recent path
     */
    void pushRecents(Path path);

    /**
     * Get the recent path list.
     * @return the recent path list
     */
    List<Path> recents();

    /**
     * Get the platform string.
     * @return the platform string
     */
    private static String platform() {
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return "windows";
        if (osName.contains("mac")) return "mac";
        if (osName.contains("linux")) return "linux";
        return "unknown";
    }

    /**
     * AbstractContext.
     */
    abstract class AbstractContext implements Context {

        /** The configuration instance. */
        private final Config config;

        /** The recent path. */
        private final Set<Path> current = new LinkedHashSet<>();

        /** The recent path. */
        private final Deque<Path> recents = new ConcurrentLinkedDeque<>();

        /**
         * Constructor.
         */
        public AbstractContext(Config config) {
            this.config = config;
        }

        @Override
        public Config config() {
            return config;
        }

        @Override
        public void opened(Path path) {
            if (!Files.exists(path)) return;
            current.add(path);
            pushRecents(path);
        }

        @Override
        public void closed(Path path) {
            current.remove(path);
        }

        @Override
        public void pushRecents(Path path) {
            boolean dup = recents.contains(path);
            recents.addFirst(path);
            if (dup) {
                var set = new LinkedHashSet<>(recents);
                recents.clear();
                recents.addAll(set);
            }
            if (recents.size() > 25) {
                int rem = recents.size() - 25;
                for (int i = 0; i < rem; i ++) {
                    recents.removeLast();
                }
            }
        }

        @Override
        public List<Path> recents() {
            return new ArrayList<>(recents);
        }

        public Path appConfPath() {

        }

    }

}
