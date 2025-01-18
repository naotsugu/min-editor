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

    /** The os name. */
    private final String osName = System.getProperty("os.name").toLowerCase();

    /** The configuration instance. */
    private AppConfig appConfig = new AppConfig(osName);

    @Override
    public AppConfig config() {
        return appConfig;
    }

    @Override
    public String osName() {
        return osName;
    }

    /**
     * The application configuration.
     */
    public static class AppConfig implements Context.Config {

        /** The config map. */
        private static final Map<String, Object> map = new ConcurrentHashMap<>();
        /** The path of config. */
        private final Path path = configDir().resolve(Path.of("config.properties"));
        /** The os name. */
        private final String osName;

        /**
         * Constructor.
         * @param osName the os name
         */
        public AppConfig(String osName) {
            this.osName = osName;
            try {
                Files.createDirectories(path.getParent());
                if (!Files.exists(path)) Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            load();
            Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        }

        /**
         * Get the path of config
         * @return the path of config
         */
        Path path() {
            return path;
        }

        @Override
        public String fontName() {
            return osName.contains("windows") ? fontNameWin()
                : osName.contains("mac") ? fontNameMac()
                : fontNameLinux();
        }

        @Override
        public double fontSize() {
            return osName.contains("windows") ? fontSizeWin()
                : osName.contains("mac") ? fontSizeMac()
                : fontSizeLinux();
        }

        @Override
        public void fontSize(double fontSize) {
            if (osName.contains("windows")) {
                map.put("fontSizeWin", fontSize);
            } else if (osName.contains("mac")) {
                map.put("fontSizeMac", fontSize);
            } else {
                map.put("fontSizeLinux", fontSize);
            }
        }

        @Override
        public String fontNameWin() {
            // return map.getOrDefault("fontNameWin", "BIZ UDGothic").toString();
            return map.getOrDefault("fontNameWin", "MS Gothic").toString();
        }

        @Override
        public double fontSizeWin() {
            return Double.parseDouble(map.getOrDefault("fontSize", 15).toString());
        }

        @Override
        public String fontNameMac() {
            return map.getOrDefault("fontNameWin", "Menlo").toString();
        }

        @Override
        public double fontSizeMac() {
            return Double.parseDouble(map.getOrDefault("fontSize", 14).toString());
        }

        @Override
        public String fontNameLinux() {
            return map.getOrDefault("fontNameWin", "monospace").toString();
        }

        @Override
        public double fontSizeLinux() {
            return Double.parseDouble(map.getOrDefault("fontSize", 15).toString());
        }

        public double windowWidth() {
            return Double.parseDouble(map.getOrDefault("app.windowWidth", 640).toString());
        }
        public void windowWidth(double val) {
            map.put("app.windowWidth", val);
        }

        public double windowHeight() {
            return Double.parseDouble(map.getOrDefault("app.windowHeight", 480.0).toString());
        }
        public void windowHeight(double val) {
            map.put("app.windowHeight", val);
        }

        public double windowPositionX() {
            return Double.parseDouble(map.getOrDefault("app.windowPositionX", -1).toString());
        }
        public void windowPositionX(double val) {
            map.put("app.windowPositionX", val);
        }

        public double windowPositionY() {
            return Double.parseDouble(map.getOrDefault("app.windowPositionY", -1).toString());
        }
        public void windowPositionY(double val) {
            map.put("app.windowPositionY", val);
        }


        private void load() {
            try {
                for (String line : Files.readAllLines(path)) {
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
                for (String line : Files.readAllLines(path)) {
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
                Files.write(path, merged);
            } catch (IOException ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }

        public void clear() {
            try {
                Files.delete(path);
                log.log(System.Logger.Level.INFO, "deleted config file.");
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
            // ? home.resolve("AppData", "Roaming", Version.appName, Version.val)
            ? home.resolve("AppData", "Local", Version.appName, Version.val)
            : osName.contains("linux")
            ? home.resolve(".config", Version.appName, Version.val)
            : osName.contains("mac")
            ? home.resolve("Library", "Application Support", Version.appName, Version.val)
            : home.resolve("." + Version.appName, Version.val);
    }

}
