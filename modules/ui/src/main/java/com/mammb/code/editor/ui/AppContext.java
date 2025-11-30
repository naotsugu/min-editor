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
package com.mammb.code.editor.ui;

import com.mammb.code.editor.core.Context;
import com.mammb.code.editor.core.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * The application context.
 * @author Naotsugu Kobayashi
 */
public class AppContext implements Context {

    /** The configuration instance. */
    private final AppConfig appConfig;

    /** The recent path. */
    private final Deque<Path> recents = new ConcurrentLinkedDeque<>();

    /**
     * Constructor.
     */
    public AppContext() {
        this.appConfig = new AppConfig();
    }

    @Override
    public AppConfig config() {
        return appConfig;
    }

    @Override
    public void pushRecents(Path path) {
        if (!Files.exists(path)) return;
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

}
