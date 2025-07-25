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
package com.mammb.code.editor.core;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import com.mammb.code.editor.core.model.TextEditorModel;

/**
 * Represents a model for an editor, defining behaviors and attributes
 * required to manage and interact with the content displayed in an editor.
 *
 * @author Naotsugu Kobayashi
 */
public interface EditorModel {

    /**
     * Paint the editor screen.
     * @param draw the draw
     */
    void paint(Draw draw);

    /**
     * Set the size of the editor.
     * @param width the width
     * @param height the height
     */
    void setSize(double width, double height);

    /**
     * Scroll next.
     * @param delta scroll delta
     */
    void scrollNext(int delta);

    /**
     * Scroll previous.
     * @param delta scroll delta
     */
    void scrollPrev(int delta);

    /**
     * Scroll to the specified line.
     * @param line the specified line
     */
    void scrollAt(int line);

    /**
     * Scroll to the specified position x.
     * @param x the specified position x
     */
    void scrollX(double x);

    /**
     * Mouse pressed at the specified position.
     * @param x the specified position x
     * @param y the specified position y
     */
    void mousePressed(double x, double y);

    /**
     * Mouse clicked at the specified position.
     * @param x the specified position x
     * @param y the specified position y
     * @param withSelect with select
     */
    void click(double x, double y, boolean withSelect);

    /**
     * Mouse clicked with control at the specified position.
     * @param x the specified position x
     * @param y the specified position y
     */
    void ctrlClick(double x, double y);

    /**
     * Mouse double-clicked at the specified position.
     * @param x the specified position x
     * @param y the specified position y
     */
    void clickDouble(double x, double y);

    /**
     * Mouse triple-clicked at the specified position.
     * @param x the specified position x
     * @param y the specified position y
     */
    void clickTriple(double x, double y);

    /**
     * Mouse dragged at the specified position.
     * @param x the specified position x
     * @param y the specified position y
     */
    void moveDragged(double x, double y);

    /**
     * Get the hover region of the editor pain.
     * @param x the specified position x
     * @param y the specified position y
     * @return the hover region
     */
    HoverOn hoverOn(double x, double y);

    /**
     * Set the visibility of the caret.
     * @param visible the visibility of the caret
     */
    void setCaretVisible(boolean visible);

    /**
     * Save the target content to a file.
     * @param path the path of a file
     */
    void save(Path path);

    /**
     * Reloads the editor's content and state.
     */
    void reload();

    /**
     * Closes the editor and releases any associated resources.
     */
    void close();

    /**
     * Saves the current state of the editor and returns it as a {@link Session} object.
     * This method allows the editor's state to be preserved and restored later.
     * @return the current state of the editor as a {@link Session}
     */
    Session stash();

    /**
     * Update fFonts.
     * @param fontMetrics the {@link FontMetrics}
     */
    void updateFonts(FontMetrics fontMetrics);

    /**
     * Set the ime on.
     */
    void imeOn();

    /**
     * Get the ime area.
     * @return the ime area
     */
    Optional<Loc> imeLoc();

    /**
     * Set the ime off.
     */
    void imeOff();

    /**
     * Get whether the ime is on or not
     * @return {@code true}, if the ime is on
     */
    boolean isImeOn();

    /**
     * Set text composed by the ime.
     * @param text composed text
     */
    void imeComposed(String text);

    /**
     * Get the session.
     * @return the session
     */
    Session getSession();

    /**
     * Associates the given {@link Session} with this {@link EditorModel}.
     * @param session the session to associate with this editor model
     * @return a new {@link EditorModel} that uses the specified session
     */
    EditorModel with(Session session);

    /**
     * Apply the action.
     * @param action the action
     */
    void apply(Action action);

    /**
     * Get the result of the query.
     * @param <R> the type of result
     * @param query the query
     * @return the result of a query
     */
    <R> R query(Query<R> query);

    /**
     * Create a new {@link EditorModel}.
     * @param fm the font metrics
     * @param scroll the screen scroll
     * @param ctx the context
     * @return a new {@link EditorModel}
     */
    static EditorModel of(FontMetrics fm, ScreenScroll scroll, Context ctx) {
        return new TextEditorModel(Content.of(), fm, scroll, ctx);
    }

    static EditorModel placeholderOf(Path path, FontMetrics fm, ScreenScroll scroll, Context ctx) {
        return new TextEditorModel(Content.readonlyPartOf(path), fm, scroll, ctx);
    }

    static EditorModel of(Path path, FontMetrics fm, ScreenScroll scroll, Context ctx, Consumer<Long> consumer) {
        return new TextEditorModel(Content.of(path, consumer), fm, scroll, ctx);
    }

}
