/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.platform;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Objects;

/**
 * The AppPath.
 * @author Naotsugu Kobayashi
 */
public abstract class AppPaths {

    /** The logger. */
    private static final System.Logger log = System.getLogger(AppPaths.class.getName());

    /** The application configuration path. */
    public static final Path applicationConfPath = applicationConfPath();

    /**
     * Provides the path to the application configuration directory,
     * which is determined based on the root configuration directory and the
     * application's version information.
     * <p>
     * The resulting path is resolved using the application's name and the
     * major and minor version numbers of the application.
     *
     * @return the application configuration path
     */
    private static Path applicationConfPath() {
        var path = confRoot().resolve(AppVersion.appName, AppVersion.val.majorAndMinor());
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Files.isDirectory(path)) {
            throw new RuntimeException("not a directory: " + path);
        }
        return path;
    }

    /**
     * Get the configuration directory for a platform.
     * @return the configuration directory
     */
    public static Path confRoot() {
        Path home = Path.of(System.getProperty("user.home"));
        if (OS.isMac()) return home.resolve("Library", "Application Support");
        if (OS.isWindows()) return home.resolve("AppData", "Local");
        return home.resolve(".config");
    }

    /**
     * Initializes the application home path by determining its location based on the provided class
     * and setting the "app.home" system property accordingly.
     * @param clazz the class whose code source determines the root directory of the application
     * @return the initialized application home path, or {@code null} if the path cannot be determined
     */
    public static Path initApplicationHomePath(Class<?> clazz) {
        var home = applicationHomePath(clazz);
        if (home != null) {
            System.setProperty("app.home", home.toString());
        }
        return home;
    }

    /**
     * Retrieves the application's home directory path by checking the "app.home" system property.
     * If the property is not set, the method initializes the application home path using the
     * provided class and sets the "app.home" property accordingly.
     *
     * @return the path to the application's home directory, or {@code null} if it cannot be determined
     */
    public static Path applicationHomePath() {
        var home = System.getProperty("app.home");
        return (home != null)
            ? Path.of(home)
            : initApplicationHomePath(AppPaths.class);
    }

    /**
     * Determines the application home path based on the location of the provided class.
     * The method evaluates different execution environments (e.g., running from a JAR,
     * from the IDE, or from a modular runtime image) and computes the application's
     * home directory accordingly.
     * @param clazz the class whose code source determines the root directory
     * @return the path to the application home directory, or {@code null} if it cannot be determined
     */
    private static Path applicationHomePath(Class<?> clazz) {
        try {

            // Get the CodeSource of the current class.
            ProtectionDomain domain = clazz.getProtectionDomain();
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

                // executed from the Mac app: min-editor.app/Contents/runtime/Contents/Home
                if (OS.isMac()) return path.getParent().getParent().getParent().getParent();
                // executed from the Win app: min-editor/runtime
                if (OS.isWindows()) return path.getParent();
                // executed from the Linux app: min-editor/lib/runtime
                if (OS.isLinux()) return path.getParent().getParent();
            }
        } catch (URISyntaxException e) {
            log.log(System.Logger.Level.ERROR, "Failed to convert URL to URI.", e);
        }
        return null;
    }

}
