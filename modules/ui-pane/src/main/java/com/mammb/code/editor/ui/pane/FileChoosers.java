/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.ui.pane;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.nio.file.Path;

/**
 * The fileChooser utility.
 * @author Naotsugu Kobayashi
 */
public class FileChoosers {

    /**
     * Choose open file.
     * @param owner the owner window
     * @param path the base path
     * @return the path
     */
    public static File fileOpenChoose(Window owner, Path path) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select file...");
        fc.setInitialDirectory(initialDirectory(path));
        return fc.showOpenDialog(owner);
    }


    /**
     * Choose save file.
     * @param owner the owner window
     * @param path the base path
     * @return the path
     */
    public static File fileSaveChoose(Window owner, Path path) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save As...");
        fc.setInitialDirectory(initialDirectory(path));
        return fc.showSaveDialog(owner);
    }


    /**
     * Select initial directory.
     * @param base the base path
     * @return the selected directory
     */
    private static File initialDirectory(Path base) {
        return (base == null)
            ? new File(System.getProperty("user.home"))
            : base.toFile().isDirectory()
                ? base.toFile()
                : base.getParent().toFile();
    }

}
