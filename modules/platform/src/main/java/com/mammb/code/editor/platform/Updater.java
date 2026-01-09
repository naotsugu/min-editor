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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The Updater.
 * @author Naotsugu Kobayashi
 */
public class Updater {

    /** The logger. */
    private static final System.Logger log = System.getLogger(Updater.class.getName());

    private static final String latest_releases_url = "https://github.com/naotsugu/min-editor/releases/latest/";

    public boolean isUpdateAvailable() {
        return AppVersion.Version.of(getLatestReleasesVersion()).isNewerThan(AppVersion.val);
    }

    /**
     * Retrieves the latest release version of the application from a specified remote URL.
     * This method sends an HTTP request to the provided URL, checks for redirection responses,
     * extracts the version tag from the "Location" header, and returns it as a string.
     * @return the latest release version as a string, or an empty string if the version could not be determined
     */
    String getLatestReleasesVersion() {
        try (var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()) {
            var request = HttpRequest.newBuilder(URI.create(latest_releases_url)).build();
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



    private static final String mac = """
        #!/bin/bash

        ZIP_FILE="min-editor-mac.zip"
        URL="https://github.com/naotsugu/min-editor/releases/latest/download/$ZIP_FILE"
        EXPAND_ROOT="min-editor"
        APP="min-editor"

        cd "$(dirname "$0")"
        set -e
        trap 'echo "Failed: Command failed on line $LINENO"; exit 1' ERR

        echo "Downloading from $URL ..."
        curl -L -o "$ZIP_FILE" "$URL"

        echo "Extracting update..."
        unzip -o "$ZIP_FILE"

        if [ -d "$EXPAND_ROOT" ]; then
            shopt -s dotglob nullglob
            cp -rf "$EXPAND_ROOT"/* .
            rm -rf "$EXPAND_ROOT"
        fi
        rm -f "$ZIP_FILE"

        echo "Starting application..."
        chmod +x "$APP"
        nohup "./$APP" > /dev/null 2>&1 &

        echo "Complete."
        """;

    private static final String ps1 = """
        $zipFile = "min-editor-win.zip"
        $url = "https://github.com/naotsugu/min-editor/releases/latest/download/$zipFile"
        $expandRoot = "min-editor"
        $app = "min-editor.exe"

        Set-Location -Path $PSScriptRoot
        $ErrorActionPreference = "Stop"

        try {

            Write-Output "Downloading from $url ..."
            Invoke-WebRequest -Uri $url -OutFile $zipFile -UseBasicParsing

            Write-Output "Extracting update..."
            Expand-Archive -Path $zipFile -DestinationPath . -Force
            Move-Item -Path "$expandRoot/*" -Destination . -Force

            Remove-Item -Path $zipFile -Force
            Remove-Item -Path $expandRoot -Force

            Write-Output "Starting application..."
            Start-Process -FilePath $app

            Write-Output "Complete."

        } catch {
            Write-Error "Failed: $_"
            exit 1
        }
        """;
}
