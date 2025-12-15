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
import java.nio.file.Path;

/**
 * The application context.
 * @author Naotsugu Kobayashi
 */
public class AppContext extends Context.AbstractContext {

    private final Path path = Config.AbstractConfig.configRoot()
        .resolve(Version.appName, Version.majorAndMinor(), "recents");

    /**
     * Constructor.
     */
    public AppContext() {
        super(new AppConfig());
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

    @Override
    public AppConfig config() {
        return (AppConfig) super.config();
    }


    public void save() {
        Files.write(path, recents().stream()
            .map(Path::toString).toList());
    }

    public void load() {
        if (!Files.exists(path)) return;
        Files.readAllLines(path).reversed().stream()
            .map(Path::of).forEach(super::pushRecents);
    }

}
