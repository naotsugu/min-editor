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
package com.mammb.code.editor.fx;

import java.util.function.Predicate;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import com.mammb.code.editor.core.Action;

import static javafx.scene.input.KeyCode.*;

/**
 * The application commands.
 * @author Naotsugu Kobayashi
 */
public class CommandKeys {

    public static Command of(KeyEvent e) {
        e.consume();

        if (e.getCode() == RIGHT) return of(Action.caretRight(e.isShiftDown(), e.isShortcutDown()));
        else if (e.getCode() == LEFT) return of(Action.caretLeft(e.isShiftDown(), e.isShortcutDown()));
        else if (e.getCode() == UP) return of(Action.caretUp(e.isShiftDown(), e.isShortcutDown()));
        else if (e.getCode() == DOWN) return of(Action.caretDown(e.isShiftDown(), e.isShortcutDown()));
        else if (e.getCode() == HOME || SC_HOME.match(e)) return of(Action.home(e.isShiftDown()));
        else if (e.getCode() == END || SC_END.match(e)) return of(Action.end(e.isShiftDown()));
        else if (e.getCode() == PAGE_UP) return of(Action.pageUp(e.isShiftDown()));
        else if (e.getCode() == PAGE_DOWN) return of(Action.pageDown(e.isShiftDown()));
        else if (e.getCode() == TAB) return of(Action.tab(e.isShiftDown()));
        else if (e.getCode() == ESCAPE) return of(Action.escape());
        else if (e.getCode() == DELETE) return of(Action.delete());
        else if (e.getCode() == BACK_SPACE) return of(Action.backspace());
        else if (e.getCode() == F1) return new Command.Help();
        else if (e.getCode() == F3) return (e.isShiftDown() || e.isShortcutDown()) ? of(Action.findPrev()) : of(Action.findNext());

        else if (SC_C.match(e)) return of(Action.copy(FxClipboard.instance));
        else if (SC_V.match(e)) return of(Action.paste(FxClipboard.instance));
        else if (SC_X.match(e)) return of(Action.cut(FxClipboard.instance));
        else if (SC_Z.match(e)) return of(Action.undo());
        else if (SC_Y.match(e) || SC_SZ.match(e)) return of(Action.redo());
        else if (SC_L.match(e)) return of(Action.toggleLayout());
        else if (SC_A.match(e)) return of(Action.selectAll());
        else if (SC_DOT.match(e)) return of(Action.repeat());
        else if (SC_O.match(e)) return new Command.OpenChoose();
        else if (SC_S.match(e)) return new Command.Save();
        else if (SC_SA.match(e)) return new Command.SaveAs();
        else if (SC_N.match(e)) return new Command.New();
        else if (SC_W.match(e)) return new Command.TabClose();
        else if (SC_F.match(e)) return new Command.Palette(Command.FindAll.class);
        else if (SC_P.match(e)) return new Command.Palette(null);
        else if (SC_COMMA.match(e)) return new Command.Config();
        else if (SC_PLUS.match(e)) return new Command.ZoomIn();
        else if (SC_MINUS.match(e)) return new Command.ZoomOut();
        else if (SC_FW.match(e)) return new Command.Forward();
        else if (SC_BW.match(e)) return new Command.Backward();

        else {
            if (keyInput.test(e)) {
                int ascii = e.getCharacter().getBytes()[0];
                if (ascii < 32 || ascii == 127) { // 127:DEL
                    if (/* ascii != 9 && */ascii != 10 && ascii != 13) { // 9:HT 10:LF 13:CR
                        return of(Action.empty());
                    }
                }
                String ch = (ascii == 13) // 13:CR
                    ? "\n"
                    : e.getCharacter();

                return ch.isEmpty()
                    ? of(Action.empty())
                    : of(Action.input(ch));
            }
        }
        return of(Action.empty());
    }

    public static Command.ActionCommand of(Action action) {
        return new Command.ActionCommand(action);
    }

    private static final KeyCombination SC_A = new KeyCharacterCombination("a", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_C = new KeyCharacterCombination("c", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_V = new KeyCharacterCombination("v", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_X = new KeyCharacterCombination("x", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_Z = new KeyCharacterCombination("z", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_Y = new KeyCharacterCombination("y", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_SZ= new KeyCharacterCombination("z", KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SC_END  = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_HOME = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_N = new KeyCharacterCombination("n", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_O = new KeyCharacterCombination("o", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_S = new KeyCharacterCombination("s", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_SA= new KeyCharacterCombination("s", KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SC_F = new KeyCharacterCombination("f", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_P = new KeyCharacterCombination("p", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_W = new KeyCharacterCombination("w", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_L = new KeyCharacterCombination("l", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_DOT = new KeyCharacterCombination(".", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_COMMA = new KeyCharacterCombination(",", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_PLUS = new KeyCharacterCombination("+", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_MINUS = new KeyCharacterCombination("-", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_FW = new KeyCharacterCombination("]", KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SC_BW = new KeyCharacterCombination("[", KeyCombination.SHORTCUT_DOWN);


    private static final Predicate<KeyEvent> controlKeysFilter = e ->
        System.getProperty("os.name").toLowerCase().startsWith("windows")
            ? !e.isControlDown() && !e.isAltDown() && !e.isMetaDown() &&
               e.getCharacter().length() == 1 && e.getCharacter().getBytes()[0] != 0
            : !e.isControlDown() && !e.isAltDown() && !e.isMetaDown();

    private static final Predicate<KeyEvent> keyInput = e ->
          e.getEventType() == KeyEvent.KEY_TYPED &&
        !(e.getCode().isFunctionKey() || e.getCode().isNavigationKey() ||
          e.getCode().isArrowKey()    || e.getCode().isModifierKey() ||
          e.getCode().isMediaKey()    || !controlKeysFilter.test(e) ||
          e.getCharacter().isEmpty());

}
