package com.mammb.code.editor.ui.app;

import com.mammb.code.editor.ui.prefs.Context;
import com.mammb.code.editor.ui.prefs.Preference;
import javafx.application.Application;

public class AppContext implements Context {

    private Preference preference = Preference.of();

    public double width;
    public double height;

    public AppContext(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public static AppContext of(Application.Parameters params) {
        return new AppContext(800, 480);
    }

    @Override
    public Preference preference() {
        return preference;
    }

    @Override
    public double regionWidth() {
        return width;
    }

    @Override
    public double regionHeight() {
        return height;
    }

}
