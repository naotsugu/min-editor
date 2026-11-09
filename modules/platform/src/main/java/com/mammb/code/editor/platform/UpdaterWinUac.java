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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 * The Windows UAC(User Account Control) Updater.
 * @author Naotsugu Kobayashi
 */
public class UpdaterWinUac implements Updater {

    @Override
    public void run() {
        try {
            var homePath = AppPaths.applicationHomePath();
            ProcessBuilder pb = new ProcessBuilder(launcherCommand(homePath));
            pb.directory(homePath.toFile());
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            // The launcher exits right after the elevated process has been started (no -Wait),
            // so this does not wait for the update itself.
            // A non-zero exit code means the UAC prompt was canceled or failed.
            int code = pb.start().waitFor();
            if (code != 0) {
                log.log(System.Logger.Level.WARNING,
                    "Elevation failed or was cancelled (exit code: {0}). Update aborted.", code);
                return;
            }

            System.exit(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds the command of the (non-elevated) launcher, which starts the update script
     * in an elevated PowerShell via {@code Start-Process -Verb RunAs}.
     * Both the launcher and the update script are passed as Base64 encoded commands.
     */
    private static List<String> launcherCommand(Path homePath) {
        // A Base64 string contains no single quote, so it is safe inside a '...' literal.
        // ErrorActionPreference=Stop makes a canceled UAC prompt return a non-zero exit code.
        var launcher = "$ErrorActionPreference = 'Stop'; "
            + "Start-Process -FilePath powershell.exe -Verb RunAs -WindowStyle Hidden "
            + "-ArgumentList '-NoProfile -EncodedCommand " + encode(updateScript(homePath)) + "'";
        return List.of("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden",
            "-EncodedCommand", encode(launcher));
    }

    /**
     * Builds the update script which runs in the elevated PowerShell.
     */
    private static String updateScript(Path homePath) {
        return """
            $TargetDir = '$HOME_PATH'
            $ErrorActionPreference = 'Stop'
            $ProgressPreference = 'SilentlyContinue'
            try {
                # Wait for the running application to exit so that its files are unlocked.
                Wait-Process -Id $PID -Timeout 60 -ErrorAction SilentlyContinue
                Set-Location -Path $TargetDir

                Invoke-WebRequest -Uri $URL$ARCHIVE_FILE -OutFile $ARCHIVE_FILE -UseBasicParsing
                Expand-Archive -Path $ARCHIVE_FILE -DestinationPath . -Force
                Copy-Item -Path (Join-Path $EXPAND_ROOT '*') -Destination . -Recurse -Force
                Remove-Item -Path $ARCHIVE_FILE -Force
                Remove-Item -Path $EXPAND_ROOT -Recurse -Force

                # Start through explorer.exe so that the application runs
                # with the original (non-elevated) privileges.
                # Start-Process -FilePath explorer.exe -ArgumentList ('"' + (Join-Path $TargetDir $APP_PATH) + '"')
                # Workaround: Launch via shortcut to avoid the title bar opening in Light Mode when started from explorer.exe.
                $lnk = Join-Path $TargetDir 'min-editor-launch.lnk'
                $sc = (New-Object -ComObject WScript.Shell).CreateShortcut($lnk)
                $sc.TargetPath = Join-Path $TargetDir $APP_PATH
                $sc.WorkingDirectory = $TargetDir
                $sc.Save()
                Start-Process -FilePath explorer.exe -ArgumentList ('"' + $lnk + '"')
            } catch {
                $_ | Out-File -FilePath (Join-Path $TargetDir $UPDATE_LOG) -Append
                exit 1
            }
            """
            .replace("$HOME_PATH", homePath.toAbsolutePath().toString().replace("'", "''"))
            .replace("$URL", DOWNLOAD_URL)
            .replace("$ARCHIVE_FILE", "min-editor-win.zip")
            .replace("$EXPAND_ROOT", "min-editor")
            .replace("$APP_PATH", "min-editor.exe")
            .replace("$UPDATE_LOG", UPDATE_LOG)
            .replace("$PID", "" + ProcessHandle.current().pid());
    }

    /**
     * Encodes the script for {@code -EncodedCommand} (Base64 of UTF-16LE text).
     */
    private static String encode(String script) {
        return Base64.getEncoder().encodeToString(
            script.getBytes(StandardCharsets.UTF_16LE));
    }

}
