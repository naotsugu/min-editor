/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.ui.ColorScheme;
import javax.swing.*;

/**
 * The application launcher.
 * @author Naotsugu Kobayashi
 */
public class AppLauncher {

    /**
     * Launch the application.
     * @param args the arguments
     */
    public void launch(String[] args) {

        System.setProperty("apple.awt.application.appearance", "system");
        if (System.getProperty("core.theme") == null) {
            System.setProperty("core.theme", ColorScheme.platform().isDark() ? "dark" : "light");
        }
        SwingUtilities.invokeLater(() -> new App(args));
    }

    /**
     * Launch the application.
     * @param args the arguments
     */
    public static void main(String[] args) {
        new AppLauncher().launch(args);
    }

}
