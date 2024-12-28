/*
 * Copyright 2022-2024 the original author or authors.
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
    Tab, Undo, Wrap, Goto, FindAll, Repeat {

    /**
     * Get occurred at.
     * @return occurred at
     */
    long occurredAt();

    default boolean isEmpty() {
        return false;
    }

    interface WithAttr<T> {
        T attr();
    }
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
    static Action wrap() {
        return new Wrap(0, System.currentTimeMillis());
    }
    static Action wrap(int width) {
        return new Wrap(width, System.currentTimeMillis());
    }
    static Action goTo(int row) {
        return new Goto(row, System.currentTimeMillis());
    }
    static Action findAll(String str) {
        return new FindAll(str, System.currentTimeMillis());
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
    static Action caretRight(boolean withSelect) {
        return new CaretRight(withSelect, System.currentTimeMillis());
    }
    static Action caretLeft(boolean withSelect) {
        return new CaretLeft(withSelect, System.currentTimeMillis());
    }
    static Action caretUp(boolean withSelect) {
        return new CaretUp(withSelect, System.currentTimeMillis());
    }
    static Action caretDown(boolean withSelect) {
        return new CaretDown(withSelect, System.currentTimeMillis());
    }
    static Action pageUp(boolean withSelect) {
        return new PageUp(withSelect, System.currentTimeMillis());
    }
    static Action pageDown(boolean withSelect) {
        return new PageDown(withSelect, System.currentTimeMillis());
    }

}
