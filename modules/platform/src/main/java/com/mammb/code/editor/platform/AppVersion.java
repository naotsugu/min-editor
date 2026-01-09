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
package com.mammb.code.editor.platform;

/**
 * The application version.
 * @author Naotsugu Kobayashi
 */
public interface AppVersion {

    /** The application name. */
    String appName = "min-editor";

    /** The version value. */
    Version val = Version.of("0.5.0");

    /**
     * An immutable representation of a version number consisting of three parts:
     * major, minor, and patch.
     * This class encapsulates the structure of a semantic version number,
     * providing a clear and type-safe way to manage version details.
     */
    record Version(String major, String minor, String patch) {

        /**
         * Parses a version string and creates a new {@code Version} instance.
         * The version string should follow the format "major.minor.patch",
         * where a dot separates each component.
         *
         * @param version the version string to be parsed
         * @return a new {@code Version} instance containing the parsed major, minor, and patch components
         * @throws ArrayIndexOutOfBoundsException if the input string does not contain exactly three parts separated by dots
         */
        public static Version of(String version) {
            String major = "0", minor = "0", patch = "0";
            if (version != null && !version.isBlank()) {
                var dots = version.chars().filter(ch -> ch == '.').count();
                if (dots < 1) major = version;
                if (dots >= 1) major = version.split("\\.")[0];
                if (dots > 1)  minor = version.split("\\.")[1];
                if (dots >= 2) patch = version.split("\\.")[2];
            }
            return new Version(major, minor, patch);
        }

        /**
         * Checks if this version is newer than the specified version.
         * @param other the version to compare against
         * @return true if this version is newer than the specified version, false otherwise
         */
        public boolean isNewerThan(Version other) {
            if (major.compareTo(other.major) > 0) {
                return true;
            } else if (major.compareTo(other.major) < 0) {
                return false;
            } else if (minor.compareTo(other.minor) > 0) {
                return true;
            } else if (minor.compareTo(other.minor) < 0) {
                return false;
            } else if (patch.compareTo(other.patch) > 0) {
                return true;
            } else if (patch.compareTo(other.patch) < 0) {
                return false;
            }
            return false;
        }

        /**
         * Combines and returns the major and minor version numbers from the version string.
         * The version string is expected to follow the format "major.minor.patch",
         * and this method concatenates the major and minor components with a dot separator.
         *
         * @return the combined major and minor version numbers as a string
         */
        public String majorAndMinor() {
            return major + "." + minor;
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }

    }

}
