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
import java.nio.file.Files;
import java.util.List;

/**
 * The *nix Updater.
 * @author Naotsugu Kobayashi
 */
public class UpdaterNix implements Updater {

    private static final String SCRIPT_NAME = "update.sh";

    @Override
    public void run() {
        try {
            var path = AppPaths.applicationHomePath().resolve(SCRIPT_NAME);
            log.log(System.Logger.Level.INFO, "Writing update script to {0}", path);
            Files.write(path, shScript().getBytes());

            ProcessBuilder pb = new ProcessBuilder(List.of("/bin/bash", SCRIPT_NAME));
            pb.redirectErrorStream(true);
            pb.directory(path.getParent().toFile());

            var logFile = path.resolveSibling(UPDATE_LOG);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
            pb.start();

            System.exit(0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String shScript() {
        return """
        #!/bin/bash
        cd "$(dirname "$0")"
        set -e

        echo "Downloading from $URL$ARCHIVE_FILE ..."
        curl -s -L -o "$ARCHIVE_FILE" "$URL$ARCHIVE_FILE"
        unzip -q -o "$ARCHIVE_FILE"

        if [ -d "$EXPAND_ROOT" ]; then
            shopt -s dotglob nullglob
            cp -rf "$EXPAND_ROOT"/* .
            rm -rf "$EXPAND_ROOT"
        fi
        rm -f "$ARCHIVE_FILE"

        echo "Starting application..."
        chmod +x "$APP_PATH"
        nohup "./$APP_PATH" > /dev/null 2>&1 &
        echo "Complete."

        rm -f update.log > /dev/null 2>&1
        exec rm -- "$0" > /dev/null 2>&1
        """
        .replace("$URL", DOWNLOAD_URL)
        .replace("$ARCHIVE_FILE", OS.isMac()
            ? OS.isArm64() ? "min-editor-mac-aarch64.zip" : "min-editor-mac.zip"
            : OS.isArm64() ? "min-editor-linux-aarch64" : "min-editor-linux.zip")
        .replace("$EXPAND_ROOT", OS.isMac() ? "min-editor.app" : "min-editor")
        .replace("$APP_PATH", OS.isMac() ? "Contents/MacOS/min-editor" : "bin/min-editor");
    }

}
