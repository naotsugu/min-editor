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
package com.mammb.code.editor.core;

/**
 * The Context.
 * @author Naotsugu Kobayashi
 */
public interface Context {

    /**
     * Get the config.
     * @return the config
     */
    Config config();

    /**
     * Get the os name.
     * @return the os name
     */
    String osName();

    // TODO recent file with session

    interface Config {
        // TODO config font
        // TODO config color
        String fontName();

        double fontSize();
        void fontSize(double fontSize);

        String fontNameWin();
        double fontSizeWin();
        String fontNameMac();
        double fontSizeMac();
        String fontNameLinux();
        double fontSizeLinux();

    }

}
