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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The HostRun.
 * @author Naotsugu Kobayashi
 */
public class HostRun {

    /** The logger. */
    private static final System.Logger log = System.getLogger(HostRun.class.getName());

    /**
     * Activates the window of a process specified by a process ID (PID).
     * This method determines the operating system (macOS or Windows) and calls the appropriate
     * platform-specific activation logic to bring the window of the specified process to the foreground.
     *
     * @param pid the process ID as a string. It must represent a valid numeric PID.
     *            If the value is {@code null} or does not match a numeric pattern, the method will exit without action.
     */
    public static void activateWindow(String pid) {

        if (pid == null || !pid.matches("\\d+")) {
            return;
        }

        if (OS.isMac()) {
            activateOnMac(pid);
        } else if (OS.isWindows()) {
            activateOnWindows(pid);
        } else if (OS.isLinux()) {
            activateOnLinux(pid);
        }

    }

    private static void activateOnMac(String pid) {
        try {
            var pb = new ProcessBuilder("open", "-a", AppVersion.appName);
            pb.redirectErrorStream(true);
            var process = pb.start();
            int ret = process.waitFor();
            log.log(System.Logger.Level.DEBUG, "Process exited with code {0}.", ret);
        } catch (Exception e) {
            log.log(System.Logger.Level.ERROR, "Failed to activate window.", e);
        }

    }

    private static void activateOnWindows(String pid) {

        var script = """
            [Threading.Thread]::CurrentThread.CurrentUICulture = 'en-US'
            $signature = @'
            [DllImport("user32.dll")]
            public static extern bool ShowWindowAsync(IntPtr hWnd, int nCmdShow);
            [DllImport("user32.dll")]
            public static extern bool SetForegroundWindow(IntPtr hWnd);
            '@
            $type = Add-Type -MemberDefinition $signature -Name "Win32ShowWindowAsync" -Namespace "Win32" -PassThru
            $process = Get-Process -Id %s -ErrorAction SilentlyContinue
            if ($process -and $process.MainWindowHandle -ne [IntPtr]::Zero) {
                $type::ShowWindowAsync($process.MainWindowHandle, 9)
                $type::SetForegroundWindow($process.MainWindowHandle)
            }
            """.formatted(pid);
        try {
            String encoded = Base64.getEncoder().encodeToString(
                script.getBytes(StandardCharsets.UTF_16LE));
            var pb = new ProcessBuilder("powershell", "-NoProfile",
                "-ExecutionPolicy", "Bypass",
                "-NonInteractive", "-EncodedCommand", encoded);
            pb.redirectErrorStream(true);
            var process = pb.start();
            int ret = process.waitFor();
            log.log(System.Logger.Level.DEBUG, "Process exited with code {0}.", ret);
        } catch (Exception e) {
            log.log(System.Logger.Level.ERROR, "Failed to activate window.", e);
        }
    }

    private static void activateOnLinux(String pid) {
        try {
            var pb = new ProcessBuilder("wmctrl", "-lp");
            var process = pb.start();
            String windowId = null;
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 3 && parts[2].equals(pid)) {
                        windowId = parts[0];
                        break;
                    }
                }
            }
            process.waitFor();
            if (windowId != null) {
                var pbActivate = new ProcessBuilder("wmctrl", "-i", "-a", windowId);
                int ret = pbActivate.start().waitFor();
                log.log(System.Logger.Level.DEBUG, "wmctrl -i -a exited with code {0}.", ret);
                return;
            }
        } catch (Exception e) {
            log.log(System.Logger.Level.DEBUG, "Failed to activate window using wmctrl.", e);
        }

        try {
            var pb = new ProcessBuilder("xdotool", "search", "--pid", pid, "windowactivate");
            int ret = pb.start().waitFor();
            log.log(System.Logger.Level.DEBUG, "xdotool exited with code {0}.", ret);
        } catch (Exception e) {
            log.log(System.Logger.Level.DEBUG, "Failed to activate window using xdotool.", e);
        }
    }

}
