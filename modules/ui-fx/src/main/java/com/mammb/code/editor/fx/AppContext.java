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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Config;
import com.mammb.code.editor.core.Context;
import com.mammb.code.editor.core.Session;
import javafx.application.Application;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The application context.
 * @author Naotsugu Kobayashi
 */
public class AppContext implements Context {

    /** The configuration instance. */
    private AppConfig appConfig = new AppConfig();

    /** The application. */
    private final Application app;

    public AppContext(Application app) {
        this.app = app;
    }

    @Override
    public AppConfig config() {
        return appConfig;
    }

    public Application getApp() {
        return app;
    }

    /**
     * The application config.
     */
    public static class AppConfig extends Config.AbstractConfig {

        /** The logger. */
        private static final System.Logger log = System.getLogger(AppConfig.class.getName());

        AppConfig() {
            super(AbstractConfig.configRoot()
                .resolve(Version.appName, Version.val, "config.properties"));
            load();
            Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        }

        /**
         * Get the window width.
         * @return the window width
         */
        public double windowWidth() {
            return Double.parseDouble(get("app.windowWidth", "640"));
        }

        /**
         * Set the window width.
         * @param val the window width
         */
        public void windowWidth(double val) {
            put("app.windowWidth", val);
        }

        /**
         * Get the window height.
         * @return the window height
         */
        public double windowHeight() {
            return Double.parseDouble(get("app.windowHeight", "480.0"));
        }

        /**
         * Set the window height.
         * @param val the window height
         */
        public void windowHeight(double val) {
            put("app.windowHeight", val);
        }

        /**
         * Get the window position x.
         * @return the window position x
         */
        public double windowPositionX() {
            return Double.parseDouble(get("app.windowPositionX", "-1"));
        }

        /**
         * Set the window position x.
         * @param val the window position x
         */
        public void windowPositionX(double val) {
            put("app.windowPositionX", val);
        }

        /**
         * Get the window position y.
         * @return the window positions y
         */
        public double windowPositionY() {
            return Double.parseDouble(get("app.windowPositionY", "-1"));
        }

        /**
         * Set the window position y.
         * @param val the window positions y
         */
        public void windowPositionY(double val) {
            put("app.windowPositionY", val);
        }

        /**
         * Get the session list.
         * @return the session list
         */
        public List<Session> sessions() {
            return Arrays.stream(get("app.sessions", "").split(File.pathSeparator.repeat(2)))
                .filter(Predicate.not(String::isBlank))
                .map(Session::of).toList();
        }

        /**
         * Set the session list.
         * @param sessions the session list
         */
        public void sessions(List<Session> sessions) {
            put("app.sessions", sessions.stream().map(Session::asString)
                .collect(Collectors.joining(File.pathSeparator.repeat(2))));
        }

        /**
         * Clear sessions.
         */
        public void clearSessions() {
            put("app.sessions", "");
            try {
                File[] files = stashPath().toFile().listFiles(File::isFile);
                if (files == null) return;
                for (File file : files) {
                    Files.deleteIfExists(file.toPath());
                }
            } catch (IOException ignore) {
                log.log(System.Logger.Level.WARNING, ignore.getMessage());
            }
        }
    }
}
