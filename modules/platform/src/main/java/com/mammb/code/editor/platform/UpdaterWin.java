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
 * The plain windows Updater.
 * @author Naotsugu Kobayashi
 */
public class UpdaterWin implements Updater {

    private static final String SCRIPT_NAME = "update.ps1";

    @Override
    public void run() {
        try {
            var path = AppPaths.applicationHomePath().resolve(SCRIPT_NAME);
            log.log(System.Logger.Level.INFO, "Writing update script to {0}", path);
            Files.write(path, psScript().getBytes());

            ProcessBuilder pb = new ProcessBuilder(
                List.of("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", SCRIPT_NAME));
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

    private static String psScript() {
        return """
        Set-Location -Path $PSScriptRoot
        $ErrorActionPreference = "Stop"
        $ProgressPreference = "SilentlyContinue"
        try {
            Write-Output "Downloading from $URL$ARCHIVE_FILE ..."
            Invoke-WebRequest -Uri $URL$ARCHIVE_FILE -OutFile $ARCHIVE_FILE -UseBasicParsing
            Expand-Archive -Path $ARCHIVE_FILE -DestinationPath . -Force
            Copy-Item -Path "$EXPAND_ROOT/*" -Destination . -Recurse -Force

            Remove-Item -Path $ARCHIVE_FILE -Force
            Remove-Item -Path $EXPAND_ROOT -Recurse -Force

            Write-Output "Starting application..."
            Start-Process -FilePath $APP_PATH
            Write-Output "Complete."
        } catch {
            Write-Error "Failed: $_"
            exit 1
        }
        Remove-Item -LiteralPath $MyInvocation.MyCommand.Path -Force *>$null
        """
        .replace("$URL", DOWNLOAD_URL)
        .replace("$ARCHIVE_FILE", "min-editor-win.zip")
        .replace("$EXPAND_ROOT", "min-editor")
        .replace("$APP_PATH", "min-editor.exe");
    }

}
