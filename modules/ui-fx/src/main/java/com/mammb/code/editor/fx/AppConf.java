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

import com.mammb.code.editor.core.Conf;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * The App configuration.
 * @author Naotsugu Kobayashi
 */
public class AppConf implements Conf {

    private final Path baseDir;
    private final Path propertiesPath;
    private final Properties properties;

    private AppConf() {
        baseDir = baseDir();
        propertiesPath = baseDir.resolve(Path.of("conf.properties"));
        properties = new Properties();

        try {
            Files.createDirectories(baseDir);
            if (Files.exists(propertiesPath)) {
                try (var r = Files.newBufferedReader(propertiesPath)) {
                    properties.load(r);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (var w = Files.newBufferedWriter(propertiesPath)) {
                properties.store(w, LocalDateTime.now().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private static Path baseDir() {
        Path home = Path.of(System.getProperty("user.home"));
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows")
            ? home.resolve("AppData", "Roaming", "min-editor", Version.val)
            : osName.contains("linux")
            ? home.resolve(".config", "min-editor", Version.val)
            : osName.contains("mac")
            ? home.resolve("Library", "Application Support", "min-editor", Version.val)
            : home.resolve("min-editor", Version.val);
    }
}
