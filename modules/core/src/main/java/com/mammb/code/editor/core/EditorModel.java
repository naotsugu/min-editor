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
package com.mammb.code.editor.core;

import com.mammb.code.editor.core.layout.Loc;
import com.mammb.code.editor.core.model.TextEditorModel;
import com.mammb.code.editor.core.syntax.Syntax;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

/**
 * The facade of editor.
 * @author Naotsugu Kobayashi
 */
public interface EditorModel {

    /**
     * Create a new {@link EditorModel}.
     * @param content the content
     * @param fm the font metrics
     * @param scroll the screen scroll
     * @return a new {@link EditorModel}
     */
    static EditorModel of(Content content, FontMetrics fm, ScreenScroll scroll) {
        return new TextEditorModel(
            content,
            fm,
            Syntax.of(content.path().map(EditorModel::extension).orElse("")),
            scroll);
    }

    void draw(Draw draw);
    void setSize(double width, double height);

    void scrollNext(int delta);
    void scrollPrev(int delta);
    void scrollAt(int line);
    void moveTo(int row);
    void scrollX(double x);
    void scrollToCaret();
    void moveCaretRight(boolean withSelect);
    void moveCaretLeft(boolean withSelect);
    void moveCaretDown(boolean withSelect);
    void moveCaretUp(boolean withSelect);
    void moveCaretHome(boolean withSelect);
    void moveCaretEnd(boolean withSelect);
    void moveCaretPageUp(boolean withSelect);
    void moveCaretPageDown(boolean withSelect);
    void selectAll();
    void click(double x, double y, boolean withSelect);
    void ctrlClick(double x, double y);
    void clickDouble(double x, double y);
    void clickTriple(double x, double y);
    void moveDragged(double x, double y);
    void setCaretVisible(boolean visible);

    void input(String text);
    void delete();
    void backspace();
    void replace(Function<String, String> fun, boolean keepSelection);
    void undo();
    void redo();
    void pasteFromClipboard(Clipboard clipboard);
    void copyToClipboard(Clipboard clipboard);
    void cutToClipboard(Clipboard clipboard);
    Optional<Path> path();
    void save(Path path);
    void escape();
    void wrap(int width);

    Optional<Loc> imeOn();
    void imeOff();
    boolean isImeOn();
    void inputImeComposed(String text);

    void findAll(String text);

    /**
     * Get the result of the query.
     * @param <R> the type of result
     * @param query the query
     * @return the result of query
     */
    <R> R query(Query<R> query);

    private static String extension(Path path) {
        return Optional.of(path.getFileName().toString())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1))
                .orElse("");
    }

}
