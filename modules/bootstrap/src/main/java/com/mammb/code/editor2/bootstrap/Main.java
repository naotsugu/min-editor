package com.mammb.code.editor2.bootstrap;

import com.mammb.code.editor2.ui.app.Launcher;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.setProperty(
            "java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");
        new Launcher().launch(args);
    }

}
