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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The application configuration.
 * Represents a configuration interface for managing properties and file paths.
 * @author Naotsugu Kobayashi
 */
public interface Config {

    /**
     * Get the font name.
     * @return the font name
     */
    String fontName();

    /**
     * The font size.
     * @return the font size
     */
    double fontSize();

    /**
     * Get the config path.
     * @return the config path
     */
    Path path();

    /**
     * Load the config.
     */
    void load();

    /**
     * Save the config.
     */
    void save();

    /**
     * Get the stash path.
     * @return the stash path
     */
    default Path stashPath() {
        Path dir = path().getParent().resolve("stash");
        if (!dir.toFile().exists()) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    /**
     * AbstractConfig.
     */
    abstract class AbstractConfig implements Config {

        /** The logger. */
        private static final System.Logger log = System.getLogger(AbstractConfig.class.getName());

        /** The prop map. */
        private final Map<String, Object> props = new ConcurrentHashMap<>();

        /** The path of props. */
        private final Path propsPath;

        /** The default font name. */
        private String defaultFontName;

        /** The default font size. */
        private double defaultFontSize;

        /**
         * Constructor.
         * @param path the path of props
         */
        protected AbstractConfig(Path path) {
            propsPath = path;
            defaultFontName = switch (Context.platform) {
                case "windows" -> "MS Gothic"; // MS Gothic | BIZ UDGothic Consolas
                case "mac" -> "Monaco";        // Menlo | Monaco | SF Mono
                default -> "monospace";
            };
            defaultFontSize = switch (Context.platform) {
                case "mac" -> 14;
                case "windows" -> 15;
                default -> 16;
            };
        }

        @Override
        public String fontName() {
            return get("fontName", defaultFontName);
        }

        /**
         * Set the font name.
         * @param fontName the font name
         */
        public void fontName(String fontName) {
            put("fontName", fontName);
        }

        /**
         * Set the default font name.
         * @param defaultFontName the default font name
         */
        public void defaultFontName(String defaultFontName) {
            if (Objects.nonNull(defaultFontName) && !defaultFontName.isBlank()) {
                this.defaultFontName = defaultFontName;
            }
        }

        @Override
        public double fontSize() {
            return Double.parseDouble(props.getOrDefault("fontSize", defaultFontSize).toString());
        }

        public void fontSize(double fontSize) {
            put("fontSize", fontSize);
        }

        /**
         * Set the default font size.
         * @param defaultFontSize the default font size
         */
        public void defaultFontSize(double defaultFontSize) {
            if (defaultFontSize > 0) {
                this.defaultFontSize = defaultFontSize;
            }
        }

        @Override
        public Path path() {
            return propsPath;
        }

        @Override
        public void load() {
            Files.createDirectories(propsPath.getParent());
            if (!Files.exists(propsPath)) {
                Files.createFile(propsPath);
                return;
            }
            for (String line : Files.readAllLines(propsPath)) {
                var str = line.trim();
                if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) continue;
                var kv = str.split("=", 2);
                props.put(kv[0], kv[1]);
            }
        }

        @Override
        public void save() {
            try {

                List<String> merged = new ArrayList<>();

                for (String line : Files.readAllLines(propsPath)) {
                    var str = line.trim();
                    if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) {
                        merged.add(line);
                        continue;
                    }
                    var kv = str.split("=", 2);
                    if (props.containsKey(kv[0])) {
                        var v = props.remove(kv[0]);
                        merged.add(kv[0] + "=" + v);
                    }
                }

                for (var entry : props.entrySet()) {
                    merged.add(entry.getKey() + "=" + entry.getValue());
                }

                Files.write(propsPath, merged);

            } catch (Exception ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }

        public void clear() {
            try {
                Files.delete(propsPath);
                log.log(System.Logger.Level.INFO, "deleted config file.");
            } catch (Exception ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }

        protected String get(String key, String defaultValue) {
            return props.getOrDefault(key, defaultValue).toString();
        }

        protected void put(String key, Object value) {
            props.put(key, value.toString());
        }

        /**
         * Get the configuration directory for a platform.
         * @return the configuration directory
         */
        public static Path configRoot() {
            Path home = Path.of(System.getProperty("user.home"));
            return switch (Context.platform) {
                case "windows" -> home.resolve("AppData", "Local");
                case "mac" -> home.resolve("Library", "Application Support");
                case "linux" -> home.resolve(".config");
                default -> home;
            };
        }

    }

}
