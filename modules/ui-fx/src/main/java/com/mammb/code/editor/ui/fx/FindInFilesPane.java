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
package com.mammb.code.editor.ui.fx;

import com.mammb.code.editor.core.FindInFiles;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * FindInFilesPane.
 * @author Naotsugu Kobayashi
 */
public class FindInFilesPane extends BorderPane {

    /** The logger. */
    private static final System.Logger log = System.getLogger(FindInFilesPane.class.getName());

    private final TextField searchField;
    private final TextField dirField;
    private final Button choseDirButton;
    private final Button findButton;
    private final TableView<SearchResult> resultsTable;
    private final ObservableList<SearchResult> results = FXCollections.observableArrayList();
    private final ProgressBar progressBar;

    private final Consumer<OpenFileRequest> onOpenFileRequest;
    private Task<Void> curentTask;

    private FindInFilesPane(Path path, Consumer<OpenFileRequest> onOpenFileRequest) {

        this.onOpenFileRequest = onOpenFileRequest;

        searchField = new TextField();
        searchField.setPromptText("regexp");
        dirField = new TextField(path.toString());
        choseDirButton = new Button("...");
        findButton = new Button("Find");
        findButton.setDefaultButton(true);
        resultsTable = buildResultsTable();
        progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        HBox difBox = new HBox(dirField, choseDirButton);
        HBox.setHgrow(dirField, Priority.ALWAYS);

        HBox inputBox = new HBox(2, searchField, difBox, findButton);
        inputBox.setPadding(new Insets(0, 2, 0, 2));
        HBox.setHgrow(searchField, Priority.SOMETIMES);
        HBox.setHgrow(difBox, Priority.ALWAYS);

        HBox progressBox = new HBox(progressBar);
        progressBox.setPadding(new Insets(0));
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBox.setPrefHeight(1);
        VBox topBox = new VBox(inputBox, progressBox);

        setTop(topBox);
        setCenter(resultsTable);

        choseDirButton.setOnAction(_ -> dirField.setText(choseDirectory(dirField.getText())));
        findButton.setOnAction(_ -> {
            if (curentTask != null && curentTask.isRunning()) {
                curentTask.cancel();
                reset();
            } else {
                String pathText = dirField.getText();
                String pattern = searchField.getText();
                if (pathText.isBlank() || pattern.isBlank()) {
                    return;
                }
                startSearchTask(pathText, pattern);
            }
        });
    }

    public static FindInFilesPane of(Path path, Consumer<OpenFileRequest> onOpenFileRequest) {
        return new FindInFilesPane(path, onOpenFileRequest);
    }

    public void openWithWindow(Window owner) {

        var stage = new Stage();
        stage.setTitle("Find In Files");
        stage.initOwner(owner);
        stage.initModality(Modality.NONE);

        Scene scene = new Scene(this);
        stage.setScene(scene);
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        stage.setOnHidden(this::cancelCurrentTask);
        stage.show();

    }

    private void startSearchTask(String path, String pattern) {

        Path root = Path.of(path);
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            return;
        }

        results.clear();
        findButton.setText("Stop");
        progressBar.setProgress(-1);
        progressBar.setVisible(true);

        Future<?> future = FindInFiles.run(root, pattern, list -> {
            var ret = list.stream()
                .map(r -> new SearchResult(r.path(), r.line(), r.col(), r.text(), r.snippet()))
                .toList();
            Platform.runLater(() -> results.addAll(ret));
        });

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                future.get();
                return null;
            }
        };
        task.setOnSucceeded(_ -> reset());
        task.setOnCancelled(_ -> reset());
        task.setOnFailed(_ -> reset());
        task.setOnCancelled(_ -> future.cancel(true));

        curentTask = task;
        new Thread(curentTask).start();
    }

    private void reset() {
        findButton.setText("Find");
        progressBar.setVisible(false);
        progressBar.setProgress(0);
        if (results.isEmpty()) {
            resultsTable.setPlaceholder(new Label("No results found."));
        }
        curentTask = null;
    }

    private void cancelCurrentTask(WindowEvent e) {
        var task = curentTask;
        if (task != null) {
            task.cancel();
        }
    }

    private TableView<SearchResult> buildResultsTable() {

        TableView<SearchResult> table = new TableView<>();
        table.setItems(results);

        TableColumn<SearchResult, String> name = new TableColumn<>("File");
        name.setCellValueFactory(cell -> cell.getValue().name);
        name.setPrefWidth(150);
        table.getColumns().add(name);

        TableColumn<SearchResult, String> path = new TableColumn<>("Path");
        path.setCellValueFactory(cell -> cell.getValue().fullPath);
        path.setPrefWidth(200);
        table.getColumns().add(path);

        TableColumn<SearchResult, Number> line = new TableColumn<>("Line");
        line.setCellValueFactory(cell -> cell.getValue().line);
        line.setPrefWidth(50);
        table.getColumns().add(line);

        TableColumn<SearchResult, Number> col = new TableColumn<>("Col");
        col.setCellValueFactory(cell -> cell.getValue().col);
        col.setPrefWidth(50);
        table.getColumns().add(col);

        TableColumn<SearchResult, String> text = new TableColumn<>("Text");
        text.setCellValueFactory(cell -> cell.getValue().text);
        text.setPrefWidth(100);
        table.getColumns().add(text);

        TableColumn<SearchResult, String> snippet = new TableColumn<>("Snippet");
        snippet.setCellValueFactory(cell -> cell.getValue().snippet);
        snippet.setPrefWidth(400);
        table.getColumns().add(snippet);

        // handle row double-click
        table.setRowFactory(_ -> {
            TableRow<SearchResult> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    if (onOpenFileRequest != null) {
                        onOpenFileRequest.accept(
                            new OpenFileRequest(
                                row.getItem().path(),
                                row.getItem().text.get(),
                                row.getItem().line.get(),
                                row.getItem().col.get(),
                                event.isShortcutDown()));
                    }
                }
            });
            return row;
        });

        return table;
    }

    record SearchResult(
            SimpleStringProperty name,
            SimpleStringProperty fullPath,
            SimpleIntegerProperty line,
            SimpleIntegerProperty col,
            SimpleStringProperty text,
            SimpleStringProperty snippet) {
        public SearchResult(Path path, long line, int col, String text, String snippet) {
            this(new SimpleStringProperty(path.getFileName().toString()),
                 new SimpleStringProperty(path.toString()),
                 new SimpleIntegerProperty((int) line),
                 new SimpleIntegerProperty(col),
                 new SimpleStringProperty(text),
                 new SimpleStringProperty(snippet.trim()));
        }
        public Path path() { return Path.of(fullPath.get()); }
    }

    public record OpenFileRequest(Path path, String text, int line, int col, boolean withShortcut) {}

    private String choseDirectory(String initial) {

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a directory path");

        var path = Path.of(initial);
        if (Files.exists(path)) {
            var dir = Files.isDirectory(path)
                ? path.toFile()
                : path.getParent().toFile();
            chooser.setInitialDirectory(dir);
        }

        File selectedDirectory = chooser.showDialog(getScene().getWindow());

        return (selectedDirectory == null)
            ? initial
            : selectedDirectory.toPath().toString();
    }

}
