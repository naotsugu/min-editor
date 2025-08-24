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
package com.mammb.code.editor.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The application pane.
 * @author Naotsugu Kobayashi
 */
public class AppPane extends JFrame {

    public AppPane(String title, Path path, AppContext ctx) throws HeadlessException {
        super(title);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        double posX = ctx.config().windowPositionX();
        double posY = ctx.config().windowPositionY();
        if (posX >= 0 && posY >= 0) {
            // init window location
            setLocation((int) posX, (int) posY);
        }

        double w = Math.max(ctx.config().windowWidth(), 91);
        double h = Math.max(ctx.config().windowHeight(), 33);
        setSize((int) w, (int) h);
        getContentPane().add(new EditorPane(ctx));
    }

}
