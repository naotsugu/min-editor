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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * FindInFilesPane.
 * @author Naotsugu Kobayashi
 */
public class FindInFilesPane extends BorderPane {

    private final TextField searchField;
    private final TextField dirField;
    private final Button findButton;
    private final TableView<SearchResult> resultsTable;
    private final ObservableList<SearchResult> results = FXCollections.observableArrayList();

    public FindInFilesPane() {

        searchField = new TextField();
        dirField = new TextField();
        findButton = new Button("Find");
        findButton.setDefaultButton(true);
        resultsTable = buildResultsTable();

        HBox inputBox = new HBox(2, dirField, searchField, findButton);
        inputBox.setPadding(new Insets(2));
        HBox.setHgrow(searchField, Priority.ALWAYS);
        HBox.setHgrow(dirField, Priority.SOMETIMES);

        setTop(inputBox);
        setCenter(resultsTable);

    }

    public void openAsWindow(Window owner) {

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

    private void cancelCurrentTask(WindowEvent e) {
    }

    private TableView<SearchResult> buildResultsTable() {

        TableView<SearchResult> table = new TableView<>();
        table.setItems(results);

        TableColumn<SearchResult, String> path = new TableColumn<>("Path");
        path.setCellValueFactory(cell -> cell.getValue().path);
        path.setPrefWidth(200);
        table.getColumns().add(path);

        TableColumn<SearchResult, Number> line = new TableColumn<>("Line");
        line.setCellValueFactory(cell -> cell.getValue().line);
        line.setPrefWidth(50);
        table.getColumns().add(line);

        TableColumn<SearchResult, String> snippet = new TableColumn<>("Snippet");
        snippet.setCellValueFactory(cell -> cell.getValue().snippet);
        snippet.setPrefWidth(100);
        table.getColumns().add(snippet);

        return table;
    }

    record SearchResult(SimpleStringProperty path, SimpleIntegerProperty line, SimpleStringProperty snippet) { }
}
