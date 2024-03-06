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
package com.mammb.code.editor.ui.app.helper;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The directory utilities.
 * @author Naotsugu Kobayashi
 */
public class Directory {

    /** logger. */
    private static final System.Logger log = System.getLogger(Directory.class.getName());


    /**
     * Open directories in OS.
     * @param path the directory path
     */
    public static void open(Path path) {
        try {
            if (Files.isDirectory(path) && Files.isReadable(path)) {
                var osn = System.getProperty("os.name").toLowerCase();
                if (osn.startsWith("windows")) {
                    new ProcessBuilder("Explorer.exe", path.toFile().getCanonicalPath())
                        .redirectErrorStream(true).start();
                } else if (osn.startsWith("mac")) {
                    new ProcessBuilder("open", path.toFile().getCanonicalPath())
                        .redirectErrorStream(true).start();
                }
            }
        } catch (Exception e) {
            log.log(System.Logger.Level.WARNING, e);
        }
    }

}
