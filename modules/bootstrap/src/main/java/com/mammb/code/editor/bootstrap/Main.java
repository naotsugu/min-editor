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
package com.mammb.code.editor.bootstrap;

import java.util.Locale;
import com.mammb.code.editor.platform.AppPaths;
import com.mammb.code.editor.platform.LockFile;
import com.mammb.code.editor.ui.fx.AppLauncher;

/**
 * The application entry point.
 * @author Naotsugu Kobayashi
 */
public class Main {

    /** The logger. */
    private static final System.Logger log = System.getLogger(Main.class.getName());

    /** The lock file. */
    private static final LockFile lockFile = new LockFile(AppPaths.applicationConfPath.resolve("lock"));

    /**
     * Launch application.
     * @param args the command line arguments
     */
    static void main(String[] args) {

        try {
            lockFile.tryLock();
        } catch (Exception e) {
            log.log(System.Logger.Level.ERROR, e);
            System.exit(1);
        }

        // output logs and button names in English
        Locale.setDefault(Locale.US);
        System.setProperty("user.country", "US");
        System.setProperty("user.language", "en");

        // setup log format
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");

        // set up an application home directory
        AppPaths.initApplicationHomePath(Main.class);

        // set up piecetable config
        System.setProperty("com.mammb.code.piecetable.core.gcInterval", "100");

        // launch application
        new AppLauncher().launch(args);

    }

}
