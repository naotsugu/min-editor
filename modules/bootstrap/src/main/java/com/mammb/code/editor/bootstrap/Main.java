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
package com.mammb.code.editor.bootstrap;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Locale;
import java.util.Optional;
import com.mammb.code.editor.fx.AppLauncher;

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
    public static void main(String[] args) {

        // output logs and button names in English
        Locale.setDefault(Locale.US);
        System.setProperty("user.country", "US");
        System.setProperty("user.language", "en");

        // setup log format
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");

        System.setProperty("min-editor.root",
            Optional.ofNullable(applicationPath()).map(Path::toString).orElse(""));

        // launch application
        new AppLauncher().launch(args);

    }

    /**
     * Gets the base path of the application, regardless of the execution environment (JAR, IDE, or jlink).
     * <ul>
     *     <li>If the application is executed from the IDE: min-editor/modules/bootstrap/build/classes/java/main</li>
     *     <li>If the application is executed from the JAR: min-editor/modules/bootstrap/build/libs/bootstrap.jar</li>
     *     <li>If the application is executed from the IMAGE: min-editor/modules/bootstrap/build/image</li>
     *     <li>If the application is executed from the MAC APP: /Applications/min-editor.app/Contents/runtime/Contents/Home</li>
     *     <li>If the application is executed from the WIN APP: c:\Program Files\min-editor</li>
     * </ul>
     * @return A Path object. Instead of returning null if resolution fails, this method throws an exception.
     * @throws IllegalStateException if the path could not be determined.
     */
    public static Path applicationPath() {
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

            return switch (protocol) {
                case "file" -> Path.of(location.toURI());
                case "jrt" -> Path.of(System.getProperty("java.home"));
                default -> null;
            };

        } catch (URISyntaxException e) {
            log.log(System.Logger.Level.ERROR, "Failed to convert URL to URI.", e);
            return null;
        }
    }

}
