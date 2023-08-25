package com.mammb.code.editor.ui.app;

import javafx.application.Application;

public class AppContext {
    public double width;
    public double height;

    public AppContext(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public static AppContext of(Application.Parameters params) {
        return new AppContext(800, 480);
    }
}
