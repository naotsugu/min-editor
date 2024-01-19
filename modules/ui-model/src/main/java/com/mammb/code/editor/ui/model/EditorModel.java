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
package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.ui.model.impl.EditorModelImpl;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.scene.canvas.GraphicsContext;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public interface EditorModel extends EditorDraw {

    // -- edit behavior -------------------------------------------------------
    /**
     * Input the string.
     * @param input the string.
     */
    void input(String input);
    /**
     * Delete a character.
     */
    void delete();
    /**
     * Backspace delete a character.
     */
    void backspace();
    /**
     * Undo.
     */
    void undo();
    /**
     * Redo.
     */
    void redo();

    // -- special edit behavior -----------------------------------------------
    /**
     * Apply editing.
     * @param editing the editing
     * @return {@code true} if edited
     */
    boolean applyEditing(Editing editing);
    /**
     * Selection replace.
     * @param string the replace text
     */
    void selectionReplace(String string);
    /**
     * Indent.
     */
    void indent();
    /**
     * Unindent.
     */
    void unindent();

    // -- caret behavior ------------------------------------------------------

    /**
     * Move the caret to the right.
     */
    void moveCaretRight();
    /**
     * Move the caret to the left.
     */
    void moveCaretLeft();
    /**
     * Move the caret to the Up.
     */
    void moveCaretUp();
    /**
     * Move the caret to the Down.
     */
    void moveCaretDown();
    /**
     * Move caret to the beginning of the line.
     */
    void moveCaretLineHome();
    /**
     * Move caret to the ending of the line.
     */
    void moveCaretLineEnd();

    // -- scroll behavior ------------------------------------------------------

    /**
     * Scroll previous a specified number of lines.
     * @param n a specified number of lines
     */
    void scrollPrev(int n);
    /**
     * Scroll next a specified number of lines.
     * @param n a specified number of lines
     */
    void scrollNext(int n);
    /**
     * Page up
     */
    void pageUp();
    /**
     * Page down
     */
    void pageDown();
    /**
     * Scroll to the specified line.
     * @param oldValue the old row value
     * @param newValue the new row value
     */
    void vScrolled(int oldValue, int newValue);

    // -- mouse behavior ------------------------------------------------------

    void click(double x, double y);
    void clickDouble(double x, double y);
    void clickTriple(double x, double y);
    void dragged(double x, double y);

    // -- select behavior ---------------------------------------------------

    void selectOn();
    void selectOff();
    void selectTo();
    void selectAll();

    // -- clipboard behavior ---------------------------------------------------
    /**
     * Paste the text from the clipboard.
     */
    void pasteFromClipboard();
    /**
     * Copy the selection text to the clipboard.
     */
    void copyToClipboard();
    /**
     * Cut the selection text to the clipboard.
     */
    void cutToClipboard();

    // -- file behavior -------------------------------------------------------
    /**
     * Save.
     */
    void save();
    /**
     * Save as.
     * @param path the path
     */
    void saveAs(Path path);
    /**
     * Get the path.
     * @return the path
     */
    Path path();
    boolean modified();

    // -- ime behavior --------------------------------------------------------

    Rect imeOn(GraphicsContext gc);
    void imeOff();
    void imeCommitted(String text);
    void imeComposed(List<ImeRun> runs);
    boolean isImeOn();

    // -- layout behavior -----------------------------------------------------

    void layoutBounds(double width, double height);
    void toggleWrap();

    // -- utilities -----------------------------------------------------------

    FindHandle findHandle();
    Rect textAreaRect();
    StateHandler stateChange();
    ScreenPoint screenPoint();
    void apply(ScreenPoint screenPoint);
    boolean peekSelection(Predicate<String> predicate);

    /**
     * Create a new partially editor model.
     * @param path the specified path
     * @return a new partially EditorModel
     */
    EditorModel partiallyWith(Path path);

    /**
     * Re-create the model at the specified path.
     * @param path the specified path
     * @return a new EditorModel
     */
    EditorModel with(Path path);

    /**
     * Create a EditorModel.
     * @param context the Context
     * @param width the width
     * @param height the height
     * @param vScroll the vertical scroll
     * @param hScroll the horizontal scroll
     * @return a EditorModel
     */
    static EditorModel of(Context context, double width, double height, ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {
        return EditorModelImpl.of(context, width, height, vScroll, hScroll);
    }

}
