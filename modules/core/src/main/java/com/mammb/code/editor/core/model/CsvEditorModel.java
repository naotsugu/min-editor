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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Clipboard;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.layout.Loc;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

/**
 * The csv editor model.
 * @author Naotsugu Kobayashi
 */
public class CsvEditorModel implements EditorModel {

    @Override
    public void draw(Draw draw) {

    }

    @Override
    public void setSize(double width, double height) {

    }

    @Override
    public void scrollNext(int delta) {

    }

    @Override
    public void scrollPrev(int delta) {

    }

    @Override
    public void scrollAt(int line) {

    }

    @Override
    public void moveTo(int row) {

    }

    @Override
    public void scrollX(double x) {

    }

    @Override
    public void scrollToCaret() {

    }

    @Override
    public void moveCaretRight(boolean withSelect) {

    }

    @Override
    public void moveCaretLeft(boolean withSelect) {

    }

    @Override
    public void moveCaretDown(boolean withSelect) {

    }

    @Override
    public void moveCaretUp(boolean withSelect) {

    }

    @Override
    public void moveCaretHome(boolean withSelect) {

    }

    @Override
    public void moveCaretEnd(boolean withSelect) {

    }

    @Override
    public void moveCaretPageUp(boolean withSelect) {

    }

    @Override
    public void moveCaretPageDown(boolean withSelect) {

    }

    @Override
    public void selectAll() {

    }

    @Override
    public void click(double x, double y, boolean withSelect) {

    }

    @Override
    public void ctrlClick(double x, double y) {

    }

    @Override
    public void clickDouble(double x, double y) {

    }

    @Override
    public void clickTriple(double x, double y) {

    }

    @Override
    public void moveDragged(double x, double y) {

    }

    @Override
    public void setCaretVisible(boolean visible) {

    }

    @Override
    public void input(String text) {

    }

    @Override
    public void delete() {

    }

    @Override
    public void backspace() {

    }

    @Override
    public void replace(Function<String, String> fun) {

    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public void pasteFromClipboard(Clipboard clipboard) {

    }

    @Override
    public void copyToClipboard(Clipboard clipboard) {

    }

    @Override
    public void cutToClipboard(Clipboard clipboard) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public Optional<Path> path() {
        return Optional.empty();
    }

    @Override
    public void save(Path path) {

    }

    @Override
    public void escape() {

    }

    @Override
    public void wrap() {

    }

    @Override
    public Optional<Loc> imeOn() {
        return Optional.empty();
    }

    @Override
    public void imeOff() {

    }

    @Override
    public boolean isImeOn() {
        return false;
    }

    @Override
    public void inputImeComposed(String text) {

    }

    @Override
    public void findAll(String text) {

    }
}