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
import java.util.Objects;
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
     * Get the ui font name.
     * @return the ui font name
     */
    public String uiFontName() {
        return get("uiFontName", "Consolas");
    }

    /**
     * Set the ui font name in the configuration.
     * @param uiFontName the ui font name
     */
    public void uiFontName(String uiFontName) {
        put("uiFontName", uiFontName);
    }

    /**
     * Get the ui font size.
     * @return the ui font size
     */
    public double uiFontSize() {
        return Double.parseDouble(get("uiFontSize", "14"));
    }

    /**
     * Set the ui font size in the configuration.
     * @param uiFontSize the ui font size to be set
     */
    public void uiFontSize(double uiFontSize) {
        put("uiFontSize", uiFontSize);
    }

    /**
     * Constructs an instance of AppConfig that manages application configuration.
     * @param appConfDir the directory where the configuration file is located
     */
    public AppConfig(Path appConfDir) {
        super(appConfDir.resolve("config.properties"));
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

}
