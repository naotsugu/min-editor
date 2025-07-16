/*
 * Copyright 2022-2024 the original author or authors.
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

/**
 * The version.
 * @author Naotsugu Kobayashi
 */
public interface Version {

    /** The application name. */
    String appName = "min-editor";

    /** The version value. */
    String val = "0.2.3";

    /**
     * Extracts and returns the major version number from the version string.
     * The version string is expected to follow the format "major.minor.patch",
     * and this method retrieves the component before the first dot.
     *
     * @return the major version number as a string
     */
    static String major() {
        return val.split("\\.")[0];
    }

    /**
     * Extracts and returns the minor version number from the version string.
     * The version string is expected to follow the format "major.minor.patch",
     * and this method retrieves the component between the first and second dots.
     *
     * @return the minor version number as a string
     */
    static String minor() {
        return val.split("\\.")[1];
    }

    /**
     * Combines and returns the major and minor version numbers from the version string.
     * The version string is expected to follow the format "major.minor.patch",
     * and this method concatenates the major and minor components with a dot separator.
     *
     * @return the combined major and minor version numbers as a string
     */
    static String majorAndMinor() {
        return major() + "." + minor();
    }

}
