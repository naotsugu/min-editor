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

import com.mammb.code.editor.ui.control.BackgroundRun;
import com.mammb.code.editor.ui.control.BlockUi;
import com.mammb.code.editor.ui.control.OverlayDialog;
import com.mammb.code.editor.ui.model.EditorModel;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.System.Logger.Level.ERROR;

/**
 * FileAction.
 * @author Naotsugu Kobayashi
 */
public class FileAction {

    /** logger. */
    private static final System.Logger log = System.getLogger(FileAction.class.getName());

    /** The pane. */
    private final Pane pane;

    /** The editor model. */
    private final EditorModel model;


    /**
     * Constructor.
     * @param pane the pane
     * @param model the editor model
     */
    private FileAction(Pane pane, EditorModel model) {
        this.pane = Objects.requireNonNull(pane);
        this.model = Objects.requireNonNull(model);
    }


    /**
     * Create a new FileBehavior.
     * @param pane the pane
     * @param model the editor model
     * @return FileBehavior
     */
    public static FileAction of(Pane pane, EditorModel model) {
        return new FileAction(pane, model);
    }


    /**
     * Open the file with a path.
     * @param path the content file path
     * @param handler the model created handler
     */
    public void open(Path path, EventHandler<WorkerStateEvent> handler) {
        confirmIfDirty(() -> updateModel(path, handler));
    }


    /**
     * Open the file.
     * @param handler the model created handler
     */
    public void open(EventHandler<WorkerStateEvent> handler) {
        confirmIfDirty(() -> {
            Path current = model.path();
            File file = FileChoosers.fileOpenChoose(pane.getScene().getWindow(), current);
            if (file != null && file.canRead()) {
                updateModel(file.toPath(), handler);
            }
        });
    }


    /**
     * Save the current model.
     * @param handler the model created handler
     */
    public void save(EventHandler<WorkerStateEvent> handler) {
        if (model.path() == null) {
            saveAs(handler);
        } else {
            runAsTask(model::save);
        }
    }


    /**
     * Save the current model as new path.
     * @param handler the model created handler
     */
    public void saveAs(EventHandler<WorkerStateEvent> handler) {
        Path current = model.path();
        File file = FileChoosers.fileSaveChoose(pane.getScene().getWindow(), current);
        if (file != null) {
            Path path = file.toPath();
            if (!pathChanged(path)) {
                return;
            } else if (equalsExtension(path, current)) {
                runAsTask(() -> model.saveAs(path));
            } else {
                saveAndUpdate(path, handler);
            }
        }
    }

    public void confirmIfDirty(Runnable runnable) {
        if (model.modified()) {
            OverlayDialog.confirm(pane,
                "Are you sure you want to discard your changes?",
                runnable);
        } else {
            runnable.run();
        }

    }

    // -- private -------------------------------------------------------------

    private void updateModel(Path path, EventHandler<WorkerStateEvent> handler) {

        if (!pathChanged(path)) {
            return;
        }

        if (size(path) > 3_000_000) {
            BlockUi.run(pane, createModelTask(() -> model.partiallyWith(path), handler));
            BackgroundRun.run(pane, createModelTask(() -> model.with(path), handler));
        } else {
            BlockUi.run(pane, createModelTask(() -> model.with(path), handler));
        }

    }


    private Task<EditorModel> createModelTask(
            Supplier<EditorModel> supplier, EventHandler<WorkerStateEvent> handler) {

        Task<EditorModel> task = new Task<>() {
            @Override protected EditorModel call() {
                return supplier.get();
            }
        };
        task.setOnSucceeded(handler);

        return task;
    }


    private void saveAndUpdate(Path path, EventHandler<WorkerStateEvent> handler) {
        BlockUi.run(pane, createModelTask(() -> {
                model.saveAs(path);
                return model.with(path);
            },
            handler));
    }


    private void runAsTask(Runnable runnable) {

        BlockUi.run(pane, new Task<Void>() {
            @Override protected Void call() {
                runnable.run();
                return null;
            }
        });

    }


    private boolean pathChanged(Path path) {
        try {
            Path org = model.path();
            if (org != null && Files.isSameFile(path, org)) {
                return false;
            }
        } catch (IOException e) {
            log.log(ERROR, e.getMessage(), e);
        }
        return true;
    }


    private static boolean equalsExtension(Path path1, Path path2) {

        if (path1 == null || path2 == null) {
            return false;
        }

        String name1 = path1.toString();
        String ext1 = name1.substring(name1.lastIndexOf('.') + 1);
        String name2 = path2.toString();
        String ext2 = name2.substring(name2.lastIndexOf('.') + 1);

        return ext1.equals(ext2);

    }


    private static long size(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
