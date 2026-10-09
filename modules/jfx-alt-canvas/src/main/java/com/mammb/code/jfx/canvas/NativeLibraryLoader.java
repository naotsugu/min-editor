/*
 * Copyright 2023-2026 the original author or authors.
 * <p>
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
package com.mammb.code.jfx.canvas;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * The NativeLibraryLoader.
 * @author Naotsugu Kobayashi
 */
final class NativeLibraryLoader {

    private static final String LIB_PATH_KEY = "com.mammb.nativeLibraryPath";

    /** The logger. */
    private static final System.Logger log = System.getLogger(NativeLibraryLoader.class.getName());

    private NativeLibraryLoader() {
    }

    public static void loadByName(String name) {

        try {
            // `mylib` for libmylib.so / mylib.dll / libmylib.dylib
            String mappedName = System.mapLibraryName(name);
            URI uri = getUrl(mappedName).toURI();
            Path libraryPath = resolveOrExtract(uri, mappedName);

            log.log(System.Logger.Level.INFO, "libraryPath: {0}", libraryPath.toAbsolutePath().toString());
            System.setProperty(LIB_PATH_KEY, libraryPath.getParent().toAbsolutePath().toString());

        } catch (Exception e) {
            throw new RuntimeException("failed to load native library: " + name, e);
        }
    }

    private static URL getUrl(String mappedName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = NativeLibraryLoader.class.getClassLoader();
        }
        return classLoader.getResource(mappedName);
    }

    private static Path resolveOrExtract(URI uri, String mappedName) throws IOException {
        if ("file".equals(uri.getScheme())) {
            // Unpacked case (e.g. IDE run with an exploded output directory):
            return Path.of(uri);
        } else {
            // "jar" (packaged inside a jar) or "jrt" (packaged inside a jlink'ed runtime image module)
            return extractToTempFile(uri.toURL(), mappedName);
        }
    }

    private static Path extractToTempFile(URL url, String mappedName) throws IOException {
        try (InputStream in = url.openStream()) {
            Path tempFile = libraryDir().resolve(mappedName);
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        }
    }

    private static Path libraryDir() throws IOException {
        String libDirStr = System.getProperty(LIB_PATH_KEY, "");
        if (!libDirStr.isBlank()) {
            Path path = Path.of(libDirStr);
            if (Files.isDirectory(path) && Files.isWritable(path)) {
                return path;
            }
        }
        Path tempDir = Files.createTempDirectory("native-lib-");
        tempDir.toFile().deleteOnExit();
        return tempDir;
    }

}