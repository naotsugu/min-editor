/*
 * Copyright 2019-2023 the original author or authors.
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

import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.ui.control.ScrollBar;
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

    void moveCaretRight();
    void moveCaretLeft();
    void moveCaretUp();
    void moveCaretDown();
    void pageUp();
    void pageDown();
    void moveCaretLineHome();
    void moveCaretLineEnd();

    void scrollPrev(int n);
    void scrollNext(int n);

    void click(double x, double y);
    void clickDouble(double x, double y);
    void dragged(double x, double y);

    void input(String input);
    void delete();
    void backspace();

    Rect imeOn(GraphicsContext gc);
    void imeOff();
    void imeCommitted(String text);
    void imeComposed(List<ImeRun> runs);
    boolean isImeOn();

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

    void undo();
    void redo();
    void indent();
    void unindent();

    void selectOn();
    void selectOff();
    void selectTo();
    void selectAll();
    boolean peekSelection(Predicate<Textual> predicate);

    /**
     * Save.
     */
    void save();

    /**
     * Save as.
     * @param path the path
     */
    void saveAs(Path path);

    Path path();
    boolean modified();

    Rect textAreaRect();
    StateHandler stateChange();
    void vScrolled(int oldValue, int newValue);
    void toggleWrap();
    void layoutBounds(double width, double height);

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


    static EditorModel of(Context context, double width, double height, ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {
        return EditorModelImpl.of(context, width, height, vScroll, hScroll);
    }

}
