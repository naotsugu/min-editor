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

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Locale;
import java.util.Objects;
import com.mammb.code.editor.ui.fx.AppLauncher;

/**
 * The application entry point.
 * @author Naotsugu Kobayashi
 */
public class Main {

    /** The logger. */
    private static final System.Logger log = System.getLogger(Main.class.getName());

    /**
     * Launch application.
     * @param args the command line arguments
     */
    static void main(String[] args) {

        // output logs and button names in English
        Locale.setDefault(Locale.US);
        System.setProperty("user.country", "US");
        System.setProperty("user.language", "en");

        // setup log format
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");

        // set up an application home directory
        if (System.getProperty("app.home") == null) {
            Path home = applicationHomePath();
            if (home != null) {
                System.setProperty("app.home", home.toString());
            }
        }

        // set up piecetable config
        System.setProperty("com.mammb.code.piecetable.core.gcInterval", "100");

        // launch application
        new AppLauncher().launch(args);

    }

    /**
     * Gets the base path of the application, regardless of the execution environment (JAR, IDE, or jlink).
     * @return A Path object. Instead of returning null if resolution fails, this method throws an exception.
     * @throws IllegalStateException if the path could not be determined.
     */
    private static Path applicationHomePath() {
        try {

            // Get the CodeSource of the current class.
            ProtectionDomain domain = Main.class.getProtectionDomain();
            CodeSource source = domain.getCodeSource();

            if (source == null) {
                log.log(System.Logger.Level.ERROR, "CodeSource is null. Cannot determine application path.");
                return null;
            }

            URL location = source.getLocation();
            String protocol = location.getProtocol();

            if (Objects.equals(protocol, "file")) {
                // executed from the IDE: min-editor/modules/bootstrap/build/classes/java/main
                // executed from the JAR: min-editor/modules/bootstrap/build/libs/bootstrap.jar
                Path path = Path.of(location.toURI());
                if (path.toString().endsWith(".jar")) {
                    return path.getParent();
                } else {
                    return Path.of(location.toURI());
                }
            } else if (Objects.equals(protocol, "jrt")) {
                Path path = Path.of(System.getProperty("java.home"));
                if (!path.toString().contains("runtime")) {
                    // executed from the IMAGE: min-editor/modules/bootstrap/build/image
                    return path;
                }
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("windows")) {
                    // executed from the Win app: min-editor/runtime
                    return path.getParent();
                }
                if (os.contains("mac")) {
                    // executed from the Mac app: min-editor.app/Contents/runtime/Contents/Home
                    return path.getParent().getParent().getParent().getParent();
                }
                if (os.contains("linux")) {
                    // executed from the Linux app: min-editor/lib/runtime
                    return path.getParent().getParent();
                }
            } else {
                return null;
            }

            return null;

        } catch (URISyntaxException e) {
            log.log(System.Logger.Level.ERROR, "Failed to convert URL to URI.", e);
            return null;
        }
    }

    private static void lock() throws Exception {
        Path lockFile = Path.of("lock");
        FileChannel fc = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        FileLock lock = fc.tryLock();
        if (lock == null) {
            System.exit(1);
        }
        long pid = ProcessHandle.current().pid();
        fc.write(ByteBuffer.wrap(String.valueOf(pid).getBytes()));
    }

}
