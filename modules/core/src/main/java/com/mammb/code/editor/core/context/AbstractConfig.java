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
package com.mammb.code.editor.core.context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The configuration.
 * @author Naotsugu Kobayashi
 */
public abstract class AbstractConfig implements Config {

    /** The logger. */
    private static final System.Logger log = System.getLogger(AbstractConfig.class.getName());

    /** The props map. */
    private final Map<String, Object> props = new ConcurrentHashMap<>();

    /** The path of props. */
    private final Path propsPath;

    /**
     * Constructor.
     * @param path the path of props
     */
    protected AbstractConfig(Path path) {
        this.propsPath = path;
    }

    @Override
    public String fontName() {
        var defaultValue = switch (Context.platform) {
            case "windows" -> "MS Gothic"; // BIZ UDGothic
            case "mac" -> "Menlo";
            default -> "monospace";
        };
        return props.getOrDefault("fontName", defaultValue).toString();
    }

    @Override
    public double fontSize() {
        var defaultValue = switch (Context.platform) {
            case "mac" -> "14";
            default -> "15";
        };
        return Double.parseDouble(props.getOrDefault("fontSize", defaultValue).toString());
    }

    @Override
    public void load() {
        try {
            for (String line : Files.readAllLines(propsPath)) {
                var str = line.trim();
                if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) continue;
                var kv = str.split("=", 2);
                props.put(kv[0], kv[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        } catch (IOException ignore) {
            log.log(System.Logger.Level.ERROR, ignore);
        }
    }

    public void clear() {
        try {
            Files.delete(propsPath);
            log.log(System.Logger.Level.INFO, "deleted config file.");
        } catch (IOException ignore) {
            log.log(System.Logger.Level.ERROR, ignore);
        }
    }


    /**
     * Get the configuration directory for platform.
     * @return the configuration directory
     */
    protected Path configRoot() {
        Path home = Path.of(System.getProperty("user.home"));
        return switch (Context.platform) {
            case "windows" -> home.resolve("AppData", "Local");
            case "mac" -> home.resolve("Library", "Application Support");
            case "linux" -> home.resolve(".config");
            default -> home;
        };
    }

}
