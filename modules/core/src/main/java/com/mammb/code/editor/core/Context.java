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
package com.mammb.code.editor.core;

/**
 * The context.
 * @author Naotsugu Kobayashi
 */
public interface Context {

    /** The platform (os) name. */
    String platform = platform();

    /**
     * Get the config.
     * @return the config
     */
    Config config();

    /**
     * Get the platform string.
     * @return the platform string
     */
    private static String platform() {
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return "windows";
        if (osName.contains("mac")) return "mac";
        if (osName.contains("linux")) return "linux";
        return "unknown";
    }

}
