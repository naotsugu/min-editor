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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Context;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The application context.
 * @author Naotsugu Kobayashi
 */
public class AppContext implements Context {

    /** The logger. */
    private static final System.Logger log = System.getLogger(AppContext.class.getName());

    /** The configuration instance. */
    private AppConfig appConfig = new AppConfig();

    @Override
    public AppConfig config() {
        return appConfig;
    }

    public static class AppConfig implements Context.Config {

        private static final Map<String, Object> map = new ConcurrentHashMap<>();
        private final Path configPath = configDir().resolve(Path.of("config.properties"));

        public AppConfig() {
            try {
                Files.createDirectories(configPath.getParent());
                if (!Files.exists(configPath)) Files.createFile(configPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            load();
            Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        }

        public double windowWidth() {
            return Double.parseDouble(map.getOrDefault("windowWidth", 640).toString());
        }
        public void windowWidth(double val) {
            map.put("windowWidth", val);
        }

        public double windowHeight() {
            return Double.parseDouble(map.getOrDefault("windowHeight", 480.0).toString());
        }
        public void windowHeight(double val) {
            map.put("windowHeight", val);
        }

        public double windowPositionX() {
            return Double.parseDouble(map.getOrDefault("windowPositionX", 320).toString());
        }
        public void windowPositionX(double val) {
            map.put("windowPositionX", val);
        }

        public double windowPositionY() {
            return Double.parseDouble(map.getOrDefault("windowPositionY", 240).toString());
        }
        public void windowPositionY(double val) {
            map.put("windowPositionY", val);
        }


        private void load() {
            try {
                for (String line : Files.readAllLines(configPath)) {
                    var str = line.trim();
                    if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) continue;
                    var kv = str.split("=", 2);
                    map.put(kv[0], kv[1]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void save() {
            try {
                List<String> merged = new ArrayList<>();
                for (String line : Files.readAllLines(configPath)) {
                    var str = line.trim();
                    if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) {
                        merged.add(line);
                        continue;
                    }
                    var kv = str.split("=", 2);
                    if (map.containsKey(kv[0])) {
                        var v = map.remove(kv[0]);
                        merged.add(kv[0] + "=" + v);
                    }
                }
                for (var entry : map.entrySet()) {
                    merged.add(entry.getKey() + "=" + entry.getValue());
                }
                Files.write(configPath, merged);
            } catch (IOException ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }
    }

    /**
     * Get the configuration directory for platform.
     * @return the configuration directory
     */
    private static Path configDir() {
        Path home = Path.of(System.getProperty("user.home"));
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows")
            ? home.resolve("AppData", "Roaming", Version.appName, Version.val)
            : osName.contains("linux")
            ? home.resolve(".config", Version.appName, Version.val)
            : osName.contains("mac")
            ? home.resolve("Library", "Application Support", Version.appName, Version.val)
            : home.resolve("." + Version.appName, Version.val);
    }

}
