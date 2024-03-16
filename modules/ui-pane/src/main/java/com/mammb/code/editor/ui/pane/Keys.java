/*
 * Copyright 2023-2024 the original author or authors.
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

import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import java.util.function.Predicate;

import static javafx.scene.input.KeyCode.*;

/**
 * Key utilities.
 * @author Naotsugu Kobayashi
 */
public class Keys {

    /** The key action type.*/
    public enum Action {
        CARET_RIGHT, CARET_LEFT, CARET_UP, CARET_DOWN,
        PAGE_UP, PAGE_DOWN,
        DELETE, BACK_SPACE, ESCAPE,
        OPEN, SAVE, SAVE_AS,
        COPY, PASTE, CUT,
        UNDO, REDO,
        HOME, END,
        SELECT_ALL,
        WRAP, EMPTY,
        NEW,
        SCROLL_UP, SCROLL_DOWN,
        UPPER, LOWER, UNIQUE, SORT, HEX, CALC,
        DEBUG,
        ;
    }

    private static final KeyCombination SC_C = new KeyCharacterCombination("c", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_V = new KeyCharacterCombination("v", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_X = new KeyCharacterCombination("x", KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_O = new KeyCharacterCombination("o", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_S = new KeyCharacterCombination("s", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_SA= new KeyCharacterCombination("s", KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);

    private static final KeyCombination SC_Z = new KeyCharacterCombination("z", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_Y = new KeyCharacterCombination("y", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_SZ= new KeyCharacterCombination("z", KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);

    private static final KeyCombination SC_N = new KeyCharacterCombination("n", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_A = new KeyCharacterCombination("a", KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_W = new KeyCharacterCombination("w", KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_END  = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_HOME = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_U  = new KeyCharacterCombination("u", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_L  = new KeyCharacterCombination("l", KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_D = new KeyCharacterCombination("d", KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_UP = new KeyCodeCombination(UP, KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_DOWN = new KeyCodeCombination(DOWN, KeyCombination.SHORTCUT_DOWN);

    private static final KeyCombination SC_0 = new KeyCharacterCombination("0", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_1 = new KeyCharacterCombination("1", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_2 = new KeyCharacterCombination("2", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_3 = new KeyCharacterCombination("3", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_4 = new KeyCharacterCombination("4", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_5 = new KeyCharacterCombination("5", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_6 = new KeyCharacterCombination("6", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_7 = new KeyCharacterCombination("7", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_8 = new KeyCharacterCombination("8", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_9 = new KeyCharacterCombination("9", KeyCombination.SHORTCUT_DOWN);

    public static final Predicate<KeyEvent> controlKeysFilter = e ->
        System.getProperty("os.name").toLowerCase().startsWith("windows")
            ? !e.isControlDown() && !e.isAltDown() && !e.isMetaDown() && e.getCharacter().length() == 1 && e.getCharacter().getBytes()[0] != 0
            : !e.isControlDown() && !e.isAltDown() && !e.isMetaDown();


    /**
     * Converts a key event into an action.
     * @param e a key event
     * @return a key action
     */
    public static Action asAction(KeyEvent e) {
        if (e.getCode() == RIGHT) return Action.CARET_RIGHT;
        else if (e.getCode() == LEFT) return Action.CARET_LEFT;
        else if (e.getCode() == UP) return Action.CARET_UP;
        else if (e.getCode() == DOWN) return Action.CARET_DOWN;
        else if (e.getCode() == DELETE) return Action.DELETE;
        else if (e.getCode() == BACK_SPACE) return Action.BACK_SPACE;
        else if (e.getCode() == ESCAPE) return Action.ESCAPE;
        else if (e.getCode() == PAGE_UP) return Action.PAGE_UP;
        else if (e.getCode() == PAGE_DOWN) return Action.PAGE_DOWN;
        else if (Keys.SC_O.match(e)) return Action.OPEN;
        else if (Keys.SC_S.match(e)) return Action.SAVE;
        else if (Keys.SC_SA.match(e)) return Action.SAVE_AS;
        else if (Keys.SC_W.match(e)) return Action.WRAP;
        else if (Keys.SC_C.match(e)) return Action.COPY;
        else if (Keys.SC_V.match(e)) return Action.PASTE;
        else if (Keys.SC_X.match(e)) return Action.CUT;
        else if (Keys.SC_Z.match(e)) return Action.UNDO;
        else if (Keys.SC_Y.match(e) || Keys.SC_SZ.match(e)) return Action.REDO;
        else if (e.getCode() == HOME || Keys.SC_HOME.match(e)) return Action.HOME;
        else if (e.getCode() == END || Keys.SC_END.match(e)) return Action.END;
        else if (Keys.SC_A.match(e)) return Action.SELECT_ALL;
        else if (Keys.SC_N.match(e)) return Action.NEW;
        else if (Keys.SC_U.match(e)) return Action.UPPER;
        else if (Keys.SC_L.match(e)) return Action.LOWER;
        else if (Keys.SC_UP.match(e)) return Action.SCROLL_UP;
        else if (Keys.SC_DOWN.match(e)) return Action.SCROLL_DOWN;
        else if (Keys.SC_1.match(e)) return Action.EMPTY;
        else if (Keys.SC_2.match(e)) return Action.EMPTY;
        else if (Keys.SC_3.match(e)) return Action.EMPTY;
        else if (Keys.SC_4.match(e)) return Action.EMPTY;
        else if (Keys.SC_5.match(e)) return Action.EMPTY;
        else if (Keys.SC_6.match(e)) return Action.EMPTY;
        else if (Keys.SC_7.match(e)) return Action.UNIQUE;
        else if (Keys.SC_8.match(e)) return Action.SORT;
        else if (Keys.SC_9.match(e)) return Action.CALC;
        else if (Keys.SC_0.match(e)) return Action.HEX;
        else if (Keys.SC_D.match(e)) return Action.DEBUG;
        else return Action.EMPTY;
    }

}
