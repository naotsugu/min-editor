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
package com.mammb.code.editor.ui.base;

import com.mammb.code.editor.core.Config;
import com.mammb.code.editor.core.Session;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The application config.
 * @author Naotsugu Kobayashi
 */
public class AppConfig extends Config.AbstractConfig {

    /** The logger. */
    private static final System.Logger log = System.getLogger(AppConfig.class.getName());

    /**
     * Constructs an instance of AppConfig that manages application configuration.
     * @param appConfDir the directory where the configuration file is located
     */
    public AppConfig(Path appConfDir) {
        super(appConfDir.resolve("config.properties"));
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

    /**
     * Get the session list.
     * @return the session list
     */
    public List<Session> sessions() {
        return Arrays.stream(get("app.sessions", "").split(File.pathSeparator.repeat(2)))
            .filter(Predicate.not(String::isBlank))
            .map(Session::valueOf).toList();
    }

    /**
     * Set the session list.
     * @param sessions the session list
     */
    public void sessions(List<Session> sessions) {
        put("app.sessions", sessions.stream().map(Session::asString)
            .collect(Collectors.joining(File.pathSeparator.repeat(2))));
    }

    /**
     * Clear sessions.
     */
    public void clearSessions() {
        put("app.sessions", "");
        try {
            File[] files = stashPath().toFile().listFiles(File::isFile);
            if (files == null) return;
            for (File file : files) {
                Files.deleteIfExists(file.toPath());
            }
        } catch (IOException ignore) {
            log.log(System.Logger.Level.WARNING, ignore.getMessage(), ignore);
        }
    }

}
