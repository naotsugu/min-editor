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
import java.util.List;

/**
 * The Updater.
 * @author Naotsugu Kobayashi
 */
public class Updater {

    /** The logger. */
    private static final System.Logger log = System.getLogger(Updater.class.getName());
    /** The releases url. */
    private static final String RELEASES_URL = "https://github.com/naotsugu/min-editor/releases/latest/";
    /** The download url. */
    private static final String DOWNLOAD_URL = RELEASES_URL + "download/";
    /** The update log file name. */
    private static final String UPDATE_LOG = "update.log";

    /**
     * Executes the update process by creating and running an update script based on the operating system.
     * <ul>
     *     <li>Generates a platform-specific update script using {@code writeScript()}.</li>
     *     <li>Determines the appropriate command to execute the script depending on the OS
     *         (PowerShell for Windows, Bash for others).</li>
     *     <li>Redirects the process output to a log file and starts the process in the script's directory.</li>
     *     <li>Terminates the application using {@code System.exit(0)} after starting the process.</li>
     * </ul>
     * If any exception occurs during the script generation or execution, a {@code RuntimeException}
     * is thrown, wrapping the original exception.
     *
     * @throws RuntimeException if an error occurs during the update script creation or execution
     */
    public static void run() {

        try {
            var scriptPath = writeScript();
            if (scriptPath == null) return;

            var command = OS.isWindows() ? ps1Command() : shCommand();

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.directory(scriptPath.getParent().toFile());

            var logFile = scriptPath.resolveSibling(UPDATE_LOG);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
            //pb.redirectInput(ProcessBuilder.Redirect.DISCARD);
            pb.start();

            System.exit(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a platform-specific update script to the application's home directory.
     * The script is generated based on the current operating system: a PowerShell script
     * for Windows or a Shell script for other operating systems.
     *
     * The method performs the following steps:
     * 1. Determines the application home path.
     * 2. Creates the script name and content specific to the operating system.
     * 3. Writes the script content to a file in the home directory.
     *
     * If the application home path is null or an I/O error occurs during the script
     * writing process, the method either returns null or throws a {@code RuntimeException}.
     *
     * @return the {@code Path} to the generated script file, or {@code null} if the
     *         home path is not determined.
     * @throws RuntimeException if an I/O error occurs while writing the script.
     */
    static Path writeScript() {
        var homePath = AppPaths.applicationHomePath();
        if (homePath == null) return null;
        var scriptName = OS.isWindows() ? "update.ps1" : "update.sh";
        var scriptBody = OS.isWindows() ? psScript() : shScript();
        try {
            var path = homePath.resolve(scriptName);
            log.log(System.Logger.Level.INFO, "Writing update script to {0}", path);
            Files.write(path, scriptBody.getBytes());
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> ps1Command() {
        return List.of("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", "update.ps1");
    }

    private static List<String> shCommand() {
        return List.of("/bin/bash", "update.sh");
    }

    /**
     * Checks whether a newer version of the application is available by comparing
     * the latest release version retrieved from a remote source with the current
     * application version.
     * @return {@code true} if a newer version is available, false otherwise
     */
    public static boolean isUpdateAvailable() {
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


    private static String shScript() {
        return """
        #!/bin/bash
        cd "$(dirname "$0")"
        set -e

        echo "Downloading from $URL$ARCHIVE_FILE ..."
        curl -L -o "$ARCHIVE_FILE" "$URL$ARCHIVE_FILE"
        unzip -o "$ARCHIVE_FILE"

        if [ -d "$EXPAND_ROOT" ]; then
            shopt -s dotglob nullglob
            cp -rf "$EXPAND_ROOT"/* .
            rm -rf "$EXPAND_ROOT"
        fi
        rm -f "ARCHIVE_FILE"

        echo "Starting application..."
        chmod +x "$APP_PATH"
        nohup "./$APP_PATH" > /dev/null 2>&1 &
        echo "Complete."

        rm -f update.log > /dev/null 2>&1
        exec rm -- "$0" > /dev/null 2>&1
        """
        .replaceAll("\\$URL", DOWNLOAD_URL)
        .replaceAll("\\$ARCHIVE_FILE", archiveName()) // min-editor-mac-aarch64.zip | min-editor-mac.zip
        .replaceAll("\\$EXPAND_ROOT", expandRoot())   // min-editor.app
        .replaceAll("\\$APP_PATH", appPath());        // Contents/MacOS/min-editor
    }

    private static String psScript() {
        return """
        Set-Location -Path $PSScriptRoot
        $ErrorActionPreference = "Stop"
        try {
            Write-Output "Downloading from $URL$ARCHIVE_FILE ..."
            Invoke-WebRequest -Uri $URL$ARCHIVE_FILE -OutFile $ARCHIVE_FILE -UseBasicParsing
            Expand-Archive -Path $ARCHIVE_FILE -DestinationPath . -Force
            Move-Item -Path "$EXPAND_ROOT/*" -Destination . -Force

            Remove-Item -Path $ARCHIVE_FILE -Force
            Remove-Item -Path $EXPAND_ROOT -Force

            Write-Output "Starting application..."
            Start-Process -FilePath $APP_PATH
            Write-Output "Complete."
        } catch {
            Write-Error "Failed: $_"
            exit 1
        }
        Remove-Item -Path update.log -Force *>$null
        Remove-Item -LiteralPath $MyInvocation.MyCommand.Path -Force *>$null
        """
        .replaceAll("\\$URL", DOWNLOAD_URL)
        .replaceAll("\\$ARCHIVE_FILE", archiveName()) // min-editor-win.zip
        .replaceAll("\\$EXPAND_ROOT", expandRoot())   // min-editor
        .replaceAll("\\$APP_PATH", appPath());        // min-editor.exe
    }

    private static String archiveName() {
        if (OS.isMac()) {
            return OS.isArm64() ? "min-editor-mac-aarch64.zip" : "min-editor-mac.zip";
        } else if (OS.isWindows()) {
            return "min-editor-win.zip";
        } else {
            return OS.isArm64() ? "min-editor-linux-aarch64" : "min-editor-linux.zip";
        }
    }

    private static String expandRoot() {
        if (OS.isMac()) {
            return "min-editor.app";
        } else if (OS.isWindows()) {
            return "min-editor";
        } else {
            return "min-editor";
        }
    }

    private static String appPath() {
        if (OS.isMac()) {
            // min-editor.app/Contents/MacOS/min-editor
            return "Contents/MacOS/min-editor";
        } else if (OS.isWindows()) {
            // min-editor/min-editor.exe
            return "min-editor.exe";
        } else {
            // min-editor/bin/min-editor
            return "bin/min-editor";
        }
    }

}
