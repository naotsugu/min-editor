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

import com.mammb.code.editor.core.model.TextEditorModel;
import com.mammb.code.editor.core.syntax.Syntax;
import java.nio.file.Path;
import java.util.Optional;

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
     * @param ctx the context
     * @return a new {@link EditorModel}
     */
    static EditorModel of(Content content, FontMetrics fm, ScreenScroll scroll, Context ctx) {
        return new TextEditorModel(
            content,
            fm,
            Syntax.of(content.path().map(EditorModel::extension).orElse("")),
            scroll,
            ctx);
    }

    static EditorModel of(Session session, FontMetrics fm, ScreenScroll scroll, Context ctx) {
        return new TextEditorModel(
            session,
            fm,
            Syntax.of(session.hasPath() ? extension(session.path()) : ""),
            scroll,
            ctx);
    }


    void draw(Draw draw);
    void setSize(double width, double height);
    void scrollNext(int delta);
    void scrollPrev(int delta);
    void scrollAt(int line);
    void scrollX(double x);
    void scrollToCaretY();
    void scrollToCaretX();
    void mousePressed(double x, double y);
    void click(double x, double y, boolean withSelect);
    void ctrlClick(double x, double y);
    void clickDouble(double x, double y);
    void clickTriple(double x, double y);
    void moveDragged(double x, double y);
    void setCaretVisible(boolean visible);

    Optional<Path> path();

    /**
     * Save the target content to a file.
     * @param path the path of file
     */
    void save(Path path);

    void updateFonts(FontMetrics fontMetrics);

    void imeOn();
    Optional<Loc> imeLoc();
    void imeOff();
    boolean isImeOn();
    void imeComposed(String text);

    Session getSession();

    void apply(Action action);

    /**
     * Get the result of the query.
     * @param <R> the type of result
     * @param query the query
     * @return the result of query
     */
    <R> R query(Query<R> query);

    /**
     * Get the extension string.
     * @param path the path
     * @return the extension string
     */
    private static String extension(Path path) {
        return Optional.of(path.getFileName().toString())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1))
                .orElse("");
    }

}
