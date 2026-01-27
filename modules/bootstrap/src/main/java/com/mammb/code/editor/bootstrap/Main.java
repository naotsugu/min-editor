/*
 * Copyright 2023-2025 the original author or authors.
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import com.mammb.code.editor.platform.AppPaths;
import com.mammb.code.editor.platform.AppVersion;
import com.mammb.code.editor.platform.OS;
import com.mammb.code.editor.ui.fx.AppLauncher;

/**
 * The application entry point.
 * @author Naotsugu Kobayashi
 */
public class Main {

    /** The logger. */
    private static final System.Logger log = System.getLogger(Main.class.getName());

    private static FileChannel channel;
    private static FileLock lock;

    /**
     * Launch application.
     * @param args the command line arguments
     */
    static void main(String[] args) {

        try {
            lock();
        } catch (Exception e) {
            log.log(System.Logger.Level.ERROR, e);
            System.exit(1);
        }

        // output logs and button names in English
        Locale.setDefault(Locale.US);
        System.setProperty("user.country", "US");
        System.setProperty("user.language", "en");

        // setup log format
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");

        // set up an application home directory
        AppPaths.initApplicationHomePath(Main.class);

        // set up piecetable config
        System.setProperty("com.mammb.code.piecetable.core.gcInterval", "100");

        // launch application
        new AppLauncher().launch(args);

    }

    private static void lock() throws Exception {
        Path lockFile = AppPaths.applicationConfPath.resolve("lock");
        channel = FileChannel.open(lockFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.READ);
        lock = channel.tryLock();
        if (lock == null) {
            if (OS.isWindows()) {
                activate(lockFile);
            }
            System.exit(0);
        }
        long pid = ProcessHandle.current().pid();
        channel.position(0);
        channel.truncate(0);
        channel.write(ByteBuffer.wrap(String.valueOf(pid).getBytes()));
        channel.force(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (lock != null) lock.close();
                if (channel != null) channel.close();
                Files.deleteIfExists(lockFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
         }));
    }

    private static void activate(Path lockFile) {
        String script = """
            $p = $null;
            try {
                $f = [System.IO.File]::Open('%s', [System.IO.FileMode]::Open, [System.IO.FileAccess]::Read, [System.IO.FileShare]::ReadWrite);
                $r = New-Object System.IO.StreamReader($f);
                $p = $r.ReadLine();
                $r.Close(); $f.Close();
            } catch {}
            $w = New-Object -ComObject WScript.Shell;
            if ($p -and $p.Trim() -match '^\\d+$') {
                if (-not $w.AppActivate([int]$p.Trim())) { $w.AppActivate('%s') }
            } else {
                $w.AppActivate('%s')
            }
            """.formatted(lockFile.toAbsolutePath(), AppVersion.appName, AppVersion.appName);
        try {
            new ProcessBuilder("powershell", "-Command", script).start();
        } catch (IOException e) {
            // ignore
        }
    }

}
