/*
 * Copyright 2022-2025 the original author or authors.
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
import java.util.function.Function;
import com.mammb.code.editor.core.model.ActionRecords.*;

/**
 * The editor action.
 * @author Naotsugu Kobayashi
 */
public sealed interface Action
    permits Backspace, CaretDown, CaretLeft, CaretRight, CaretUp, Copy, Cut, Delete, Empty,
    End, Escape, Home, Input, PageDown, PageUp, Paste, Redo, Replace, Save, SelectAll,
    Tab, Undo, WrapLine, Goto,
    FindAll, FindNext, FindPrev,
    Repeat {

    /**
     * Get occurred at.
     * @return occurred at
     */
    long occurredAt();

    /**
     * Gets whether the action is empty or not.
     * @return {@code true}, if this action is empty
     */
    default boolean isEmpty() {
        return this instanceof Empty;
    }

    /**
     * Interface to be attributed.
     * @param <T> the type of attribute
     */
    interface WithAttr<T> {
        T attr();
    }

    /**
     * Marker interface representing repeatability.
     */
    interface Repeatable { }

    interface WithSelect {
        boolean withSelect();
    }

    static Action empty() {
        return new Empty(0);
    }
    static Action input(String string) {
        return new Input(string, System.currentTimeMillis());
    }
    static Action replace(Function<String, String> fun, boolean keepSelect) {
        return new Replace(fun, keepSelect, System.currentTimeMillis());
    }
    static Action delete() {
        return new Delete(System.currentTimeMillis());
    }
    static Action backspace() {
        return new Backspace(System.currentTimeMillis());
    }
    static Action undo() {
        return new Undo(System.currentTimeMillis());
    }
    static Action redo() {
        return new Redo(System.currentTimeMillis());
    }
    static Action selectAll() {
        return new SelectAll(System.currentTimeMillis());
    }
    static Action escape() {
        return new Escape(System.currentTimeMillis());
    }
    static Action repeat() {
        return new Repeat(System.currentTimeMillis());
    }
    static Action save(Path path) {
        return new Save(path, System.currentTimeMillis());
    }
    static Action wrapLine() {
        return new WrapLine(0, System.currentTimeMillis());
    }
    static Action wrapLine(int width) {
        return new WrapLine(width, System.currentTimeMillis());
    }
    static Action goTo(int row) {
        return new Goto(row, System.currentTimeMillis());
    }
    static Action findAll(String str, boolean caseSensitive) {
        return new FindAll(FindSpec.of(str, caseSensitive), System.currentTimeMillis());
    }
    static Action findNext(String str, boolean caseSensitive) {
        return new FindNext(FindSpec.of(str, caseSensitive), System.currentTimeMillis());
    }
    static Action findPrev(String str, boolean caseSensitive) {
        return new FindPrev(FindSpec.of(str, caseSensitive), System.currentTimeMillis());
    }
    static Action findNext() {
        return new FindNext(FindSpec.EMPTY, System.currentTimeMillis());
    }
    static Action findPrev() {
        return new FindPrev(FindSpec.EMPTY, System.currentTimeMillis());
    }
    static Action findAllRegex(String str) {
        return new FindAll(FindSpec.regexpOf(str), System.currentTimeMillis());
    }
    static Action findNextRegex(String str) {
        return new FindNext(FindSpec.regexpOf(str), System.currentTimeMillis());
    }
    static Action findPrevRegex(String str) {
        return new FindPrev(FindSpec.regexpOf(str), System.currentTimeMillis());
    }
    static Action copy(Clipboard clipboard) {
        return new Copy(clipboard, System.currentTimeMillis());
    }
    static Action cut(Clipboard clipboard) {
        return new Cut(clipboard, System.currentTimeMillis());
    }
    static Action paste(Clipboard clipboard) {
        return new Paste(clipboard, System.currentTimeMillis());
    }
    static Action home(boolean withSelect) {
        return new Home(withSelect, System.currentTimeMillis());
    }
    static Action end(boolean withSelect) {
        return new End(withSelect, System.currentTimeMillis());
    }
    static Action tab(boolean withSelect) {
        return new Tab(withSelect, System.currentTimeMillis());
    }
    static Action caretRight(boolean withSelect, boolean withShortcut) {
        return new CaretRight(withSelect, withShortcut, System.currentTimeMillis());
    }
    static Action caretLeft(boolean withSelect, boolean withShortcut) {
        return new CaretLeft(withSelect, withShortcut, System.currentTimeMillis());
    }
    static Action caretUp(boolean withSelect, boolean withShortcut) {
        return new CaretUp(withSelect, withShortcut, System.currentTimeMillis());
    }
    static Action caretDown(boolean withSelect, boolean withShortcut) {
        return new CaretDown(withSelect, withShortcut, System.currentTimeMillis());
    }
    static Action pageUp(boolean withSelect) {
        return new PageUp(withSelect, System.currentTimeMillis());
    }
    static Action pageDown(boolean withSelect) {
        return new PageDown(withSelect, System.currentTimeMillis());
    }

}
