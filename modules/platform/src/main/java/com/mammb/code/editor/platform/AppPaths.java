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

import java.net.URISyntaxException;
import java.net.URL;
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
    public static Path applicationConfPath() {
        return confRoot().resolve(AppVersion.appName, AppVersion.val.majorAndMinor());
    }

    /**
     * Get the configuration directory for a platform.
     * @return the configuration directory
     */
    public static Path confRoot() {
        Path home = Path.of(System.getProperty("user.home"));
        return switch (OS.CURRENT) {
            case WINDOWS -> home.resolve("AppData", "Local");
            case MAC -> home.resolve("Library", "Application Support");
            case LINUX -> home.resolve(".config");
            default -> home;
        };
    }

    /**
     * Gets the base path of the application, regardless of the execution environment (JAR, IDE, or jlink).
     * @return A Path object. Instead of returning null if resolution fails, this method throws an exception.
     * @throws IllegalStateException if the path could not be determined.
     */
    public static Path applicationHomePath() {
        try {

            // Get the CodeSource of the current class.
            ProtectionDomain domain = AppPaths.class.getProtectionDomain();
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
                return switch (OS.CURRENT) {
                    // executed from the Mac app: min-editor.app/Contents/runtime/Contents/Home
                    case MAC -> path.getParent().getParent().getParent().getParent();
                    // executed from the Win app: min-editor/runtime
                    case WINDOWS -> path.getParent();
                    // executed from the Linux app: min-editor/lib/runtime
                    case LINUX -> path.getParent().getParent();
                    default -> null;
                };
            } else {
                return null;
            }

        } catch (URISyntaxException e) {
            log.log(System.Logger.Level.ERROR, "Failed to convert URL to URI.", e);
            return null;
        }
    }

}
