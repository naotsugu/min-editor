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
package com.mammb.code.editor.core.context;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The configuration.
 * @author Naotsugu Kobayashi
 */
public abstract class AbstractConfig implements Config {

    /** The props map. */
    private final Map<String, Object> props = new ConcurrentHashMap<>();

    /** The path of props. */
    private final Path propsPath;

    /**
     * Constructor.
     * @param path the path of props
     */
    protected AbstractConfig(Path path) {
        this.propsPath = path;
    }

    @Override
    public String fontName() {
        var defaultValue = switch (Context.platform) {
            case "windows" -> "MS Gothic"; // BIZ UDGothic
            case "mac" -> "Menlo";
            default -> "monospace";
        };
        return props.getOrDefault("fontName", defaultValue).toString();
    }

    @Override
    public double fontSize() {
        var defaultValue = switch (Context.platform) {
            case "mac" -> "14";
            default -> "15";
        };
        return Double.parseDouble(props.getOrDefault("fontSize", defaultValue).toString());
    }

}
