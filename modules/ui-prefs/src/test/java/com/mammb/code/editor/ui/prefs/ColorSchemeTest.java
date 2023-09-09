package com.mammb.code.editor.ui.prefs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import static org.junit.jupiter.api.condition.OS.MAC;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

class ColorSchemeTest {

    @Test @EnabledOnOs(MAC)
    void macOsMode() {
        ColorScheme.macOsMode();
    }

    @Test @EnabledOnOs(WINDOWS)
    void windowsMode() {
        ColorScheme.windowsMode();
    }

}
