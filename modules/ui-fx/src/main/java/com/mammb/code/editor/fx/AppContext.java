/*
 * Copyright 2023-2024 the original author or authors.
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
import javafx.application.Application;

/**
 * The application context.
 * @author Naotsugu Kobayashi
 */
public class AppContext implements Context {

    /** The configuration instance. */
    private AppConfig appConfig = new AppConfig();

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

    public static class AppConfig extends Config.AbstractConfig {

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
         * @return the window position y
         */
        public double windowPositionY() {
            return Double.parseDouble(get("app.windowPositionY", "-1"));
        }

        /**
         * Set the window position y.
         * @param val the window position y
         */
        public void windowPositionY(double val) {
            put("app.windowPositionY", val);
        }

    }

}
