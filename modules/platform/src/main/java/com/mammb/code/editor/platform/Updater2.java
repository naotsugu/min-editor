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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Updater.
 * @author Naotsugu Kobayashi
 */
public interface Updater2 {

    /** The logger. */
    System.Logger log = System.getLogger(Updater.class.getName());

    /** The releases url. */
    String RELEASES_URL = "https://github.com/naotsugu/min-editor/releases/latest/";
    /** The download url. */
    String DOWNLOAD_URL = RELEASES_URL + "download/";
    /** The update log file name. */
    String UPDATE_LOG = "update.log";

    static Updater2 of() {
        var home = AppPaths.applicationHomePath();
        if (OS.isWindows()) {
            if (isWritableDir(home)) {
                return new UpdaterWin();
            } else {
                return new UpdaterUac();
            }
        } else {
            return new UpdaterNix();
        }
    }

    /**
     * Executes the update process by creating and running an update script based on the operating system.
     * @throws RuntimeException if an error occurs during the update script creation or execution
     */
    void run();

    /**
     * Checks whether a newer version of the application is available by comparing
     * the latest release version retrieved from a remote source with the current
     * application version.
     * @return {@code true} if a newer version is available, false otherwise
     */
    static boolean isUpdateAvailable() {
        return AppVersion.Version.of(getLatestReleasesVersion())
            .isNewerThan(AppVersion.val);
    }

    /**
     * Retrieves the latest release version of the application from a specified remote URL.
     * This method sends an HTTP request to the provided URL, checks for redirection responses,
     * extracts the version tag from the "Location" header, and returns it as a string.
     * @return the latest release version as a string, or an empty string if the version could not be determined
     */
    static String getLatestReleasesVersion() {
        try (var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()) {

            var request = HttpRequest.newBuilder(URI.create(RELEASES_URL)).build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 302 || response.statusCode() == 301) {

                var location = response.headers().firstValue("Location").orElse("");

                var index = location.lastIndexOf('/');
                if (index > 0 && index< location.length() - 2) {
                    var versionTag = location.substring(index + 1);
                    if (versionTag.startsWith("v")) {
                        return versionTag.substring(1);
                    }
                }

            }
        } catch (Exception e) {
            log.log(System.Logger.Level.ERROR, e);
        }
        return "";
    }

    /**
     * Checks whether files can really be created in the directory.
     * {@code Files.isWritable()} may not reflect ACLs reliably, so a probe file is also created.
     */
    private static boolean isWritableDir(Path dir) {
        if (!Files.isDirectory(dir) || !Files.isWritable(dir)) return false;
        try {
            var probe = Files.createTempFile(dir, ".update-check", ".tmp");
            Files.deleteIfExists(probe);
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

}
