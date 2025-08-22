package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.ui.Version;
import javax.swing.*;

public class App {

    private final JFrame mainFrame;

    App() {
        mainFrame = new JFrame(Version.appName);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

}
