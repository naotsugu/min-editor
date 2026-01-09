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
package com.mammb.code.editor.ui.base;

import com.mammb.code.editor.core.Config;
import com.mammb.code.editor.core.Context;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.platform.AppVersion;
import java.nio.file.Path;

/**
 * The application context.
 * @author Naotsugu Kobayashi
 */
public class AppContext extends Context.AbstractContext {

    /** The path where the application's "recents" configuration is stored. */
    private static final Path recentsConfPath = appConfDir().resolve("recents");

    /**
     * Constructor.
     */
    public AppContext() {
        super(new AppConfig(appConfDir()));
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

    @Override
    public AppConfig config() {
        return (AppConfig) super.config();
    }

    /**
     * Persists the recent file paths to a predefined location on the filesystem.
     * The file paths are retrieved from the list of recent entries, converted to their
     * string representations, and then written to a file.
     * <p>
     * The data is stored at a location defined by the application configuration, ensuring
     * the recents list can be persisted across different application sessions.
     * <p>
     * This method is typically invoked during an application shutdown to save the current state.
     * It overwrites the existing file if it already exists.
     */
    public void save() {
        Files.write(recentsConfPath, recents().stream()
            .map(Path::toString).toList());
    }

    /**
     * Loads the list of recent file paths from a predefined location on the filesystem.
     * If the file at the specified location does not exist, the method exits without any action.
     * <p>
     * The method reads the content of the file line by line, processes each line into a {@link Path} object,
     * and adds it to the list of recent entries in reverse order of appearance in the file.
     * <p>
     * This functionality is typically used to restore the list of recently accessed files
     * when the application starts, ensuring that the application can resume with the previous state.
     */
    public void load() {
        if (!Files.exists(recentsConfPath)) return;
        Files.readAllLines(recentsConfPath).reversed().stream()
            .map(Path::of).forEach(super::pushRecents);
    }

    /**
     * Resolves and returns the path to the application configuration directory.
     * The path is determined based on the application's name and version.
     * @return the path to the application configuration directory
     */
    public static Path appConfDir() {
        return Config.AbstractConfig.configRoot().resolve(AppVersion.appName, AppVersion.val.majorAndMinor());
    }

}
