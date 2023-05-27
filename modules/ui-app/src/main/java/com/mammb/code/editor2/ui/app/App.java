package com.mammb.code.editor2.ui.app;

import com.mammb.code.editor2.ui.pane.EditorPane;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = new EditorPane(stage);
        Scene scene = new Scene(parent, 800, 480);
        stage.setScene(scene);
        stage.show();
    }

}
