/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.ui.Command;
import java.awt.event.KeyEvent;
import java.util.function.Predicate;

import static java.awt.event.KeyEvent.*;

/**
 * The application commands.
 * @author Naotsugu Kobayashi
 */
public class CommandKeys {

    public static Command of(KeyEvent e) {

        e.consume();

        if (e.getKeyCode() == VK_RIGHT) return of(Action.caretRight(e.isShiftDown(), e.isControlDown()));
        else if (e.getKeyCode() == VK_LEFT) return of(Action.caretLeft(e.isShiftDown(), e.isControlDown()));
        else if (e.getKeyCode() == VK_UP) return of(Action.caretUp(e.isShiftDown(), e.isControlDown()));
        else if (e.getKeyCode() == VK_DOWN) return of(Action.caretDown(e.isShiftDown(), e.isControlDown()));
        else if (e.getKeyCode() == VK_HOME || SC_HOME.test(e)) return of(Action.home(e.isShiftDown()));
        else if (e.getKeyCode() == VK_END || SC_END.test(e)) return of(Action.end(e.isShiftDown()));
        else if (e.getKeyCode() == VK_PAGE_UP) return of(Action.pageUp(e.isShiftDown(), e.isControlDown()));
        else if (e.getKeyCode() == VK_PAGE_DOWN) return of(Action.pageDown(e.isShiftDown(), e.isControlDown()));
        else if (e.getKeyCode() == VK_TAB) return of(Action.tab(e.isShiftDown()));
        else if (e.getKeyCode() == VK_ESCAPE) return of(Action.escape());
        else if (e.getKeyCode() == VK_DELETE) return of(Action.delete());
        else if (e.getKeyCode() == VK_BACK_SPACE) return of(Action.backspace());
        else if (e.getKeyCode() == VK_F1) return new Command.Help();
        else if (e.getKeyCode() == VK_F3) return (e.isShiftDown() || e.isControlDown()) ? of(Action.findPrev()) : of(Action.findNext());

        else if (SC_C.test(e)) return of(Action.copy(SgClipboard.instance));
        else if (SC_V.test(e)) return of(Action.paste(SgClipboard.instance, false));
        else if (SC_SV.test(e)) return of(Action.paste(SgClipboard.instance, true));
        else if (SC_X.test(e)) return of(Action.cut(SgClipboard.instance));
        else if (SC_Z.test(e)) return of(Action.undo());
        else if (SC_Y.test(e) || SC_SZ.test(e)) return of(Action.redo());
        else if (SC_L.test(e)) return of(Action.toggleLayout());
        else if (SC_A.test(e)) return of(Action.selectAll());
        else if (SC_DOT.test(e)) return of(Action.repeat());
        else if (SC_O.test(e)) return new Command.OpenChoose();
        else if (SC_S.test(e)) return new Command.Save();
        else if (SC_SA.test(e)) return new Command.SaveAs();
        else if (SC_N.test(e)) return new Command.New();
        else if (SC_W.test(e)) return new Command.TabClose();
        else if (SC_F.test(e)) return new Command.Palette(Command.FindAll.class);
        else if (SC_P.test(e) || SC_R.test(e)) return new Command.Palette(null);
        else if (SC_COMMA.test(e)) return new Command.Config();
        else if (SC_PLUS.test(e)) return new Command.ZoomIn();
        else if (SC_MINUS.test(e)) return new Command.ZoomOut();
        else if (SC_FW.test(e)) return new Command.Forward();
        else if (SC_BW.test(e)) return new Command.Backward();

        else {
            if (keyInput.test(e)) {
                int ascii = e.getKeyChar();
                if (ascii < 32 || ascii == 127) { // 127:DEL
                    if (/* ascii != 9 && */ascii != 10 && ascii != 13) { // 9:HT 10:LF 13:CR
                        return of(Action.empty());
                    }
                }
                String ch = (ascii == 13) // 13:CR
                    ? "\n"
                    : String.valueOf((char) ascii);

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

    static final Predicate<KeyEvent> SC_A = e -> e.getKeyCode() == VK_A && e.isControlDown();
    static final Predicate<KeyEvent> SC_C = e -> e.getKeyCode() == VK_C && e.isControlDown();
    static final Predicate<KeyEvent> SC_V = e -> e.getKeyCode() == VK_V && e.isControlDown();
    static final Predicate<KeyEvent> SC_SV = e -> e.getKeyCode() == VK_V && e.isControlDown() && e.isShiftDown();
    static final Predicate<KeyEvent> SC_X = e -> e.getKeyCode() == VK_X && e.isControlDown();
    static final Predicate<KeyEvent> SC_Z = e -> e.getKeyCode() == VK_Z && e.isControlDown();
    static final Predicate<KeyEvent> SC_Y = e -> e.getKeyCode() == VK_Y && e.isControlDown();
    static final Predicate<KeyEvent> SC_SZ = e -> e.getKeyCode() == VK_Z && e.isControlDown() && e.isShiftDown();
    static final Predicate<KeyEvent> SC_END  = e -> e.getKeyCode() == VK_RIGHT && e.isControlDown();
    static final Predicate<KeyEvent> SC_HOME = e -> e.getKeyCode() == VK_LEFT && e.isControlDown();
    static final Predicate<KeyEvent> SC_N = e -> e.getKeyCode() == VK_N && e.isControlDown();
    static final Predicate<KeyEvent> SC_O = e -> e.getKeyCode() == VK_O && e.isControlDown();
    static final Predicate<KeyEvent> SC_S = e -> e.getKeyCode() == VK_S && e.isControlDown();
    static final Predicate<KeyEvent> SC_SA= e -> e.getKeyCode() == VK_S && e.isControlDown() && e.isShiftDown();
    static final Predicate<KeyEvent> SC_F = e -> e.getKeyCode() == VK_F && e.isControlDown();
    static final Predicate<KeyEvent> SC_P = e -> e.getKeyCode() == VK_P && e.isControlDown();
    static final Predicate<KeyEvent> SC_R = e -> e.getKeyCode() == VK_R && e.isControlDown();
    static final Predicate<KeyEvent> SC_W = e -> e.getKeyCode() == VK_W && e.isControlDown();
    static final Predicate<KeyEvent> SC_L = e -> e.getKeyCode() == VK_L && e.isControlDown();
    static final Predicate<KeyEvent> SC_DOT = e -> e.getKeyCode() == VK_PERIOD && e.isControlDown();
    static final Predicate<KeyEvent> SC_COMMA = e -> e.getKeyCode() == VK_COMMA && e.isControlDown();
    static final Predicate<KeyEvent> SC_PLUS = e -> e.getKeyCode() == VK_PLUS && e.isControlDown();
    static final Predicate<KeyEvent> SC_MINUS = e -> e.getKeyCode() == VK_MINUS && e.isControlDown();
    static final Predicate<KeyEvent> SC_FW = e -> e.getKeyCode() == VK_CLOSE_BRACKET && e.isControlDown();
    static final Predicate<KeyEvent> SC_BW = e -> e.getKeyCode() == VK_OPEN_BRACKET && e.isControlDown();

    private static final Predicate<KeyEvent> controlKeysFilter = e ->
        System.getProperty("os.name").toLowerCase().startsWith("windows")
            ? !e.isControlDown() && !e.isAltDown() && !e.isMetaDown() &&
            e.getKeyChar() != 0
            : !e.isControlDown() && !e.isAltDown() && !e.isMetaDown();

    private static final Predicate<KeyEvent> keyInput = e ->
        e.getID() == KeyEvent.KEY_TYPED &&
            !(e.isActionKey() || !controlKeysFilter.test(e));

}
