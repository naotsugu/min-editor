package com.mammb.code.editor.ui.model;

/**
 * The Ime run.
 * @param offset
 * @param text
 * @param type
 */
public record ImeRun(int offset, String text, RunType type) {

    public int length() {
        return text.length();
    }

    /** The RunType. */
    public enum RunType {
        /** The selected converted input method text. */
        SELECTED_CONVERTED,
        /** The unselected converted input method text. */
        UNSELECTED_CONVERTED,
        /** The selected raw input method text. */
        SELECTED_RAW,
        /** The unselected raw input method text. */
        UNSELECTED_RAW,
    }

}

