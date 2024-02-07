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
package com.mammb.code.editor.model.edit.impl;

import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditTo;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CompoundEdit.
 * @param edits the elements of compound edit
 * @param occurredOn the occurredOn.
 * @author Naotsugu Kobayashi
 */
public record CompoundEdit(
        List<? extends Edit> edits,
        long occurredOn) implements Edit {


    @Override
    public OffsetPoint point() {
        return edits.get(0).point();
    }

    @Override
    public int length() {
        return edits.stream().mapToInt(Edit::length).sum();
    }

    @Override
    public Edit flip() {
        var list = edits.stream().map(Edit::flip).collect(Collectors.toList());
        Collections.reverse(list);
        return new CompoundEdit(list, occurredOn);
    }

    @Override
    public void apply(EditTo editTo) {
        edits.forEach(edit -> edit.apply(editTo));
    }

    @Override
    public Textual applyTo(Textual textual) {
        if (acrossRows()) {
            throw new IllegalStateException("Should be pre-flashed");
        }
        return edits.get(0).applyTo(textual);
    }


    @Override
    public boolean canMerge(Edit other) {
        if (other instanceof EmptyEdit) {
            return true;
        }
        if (other instanceof CompoundEdit compound &&
                edits.size() == compound.edits.size()) {
            for (int i = 0; i < edits.size(); i++) {
                // can all elements merge
                if (!edits.get(i).canMerge(compound.edits.get(0))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public Edit merge(Edit other) {
        if (other instanceof EmptyEdit) {
            return this;
        }
        CompoundEdit compound = (CompoundEdit) other;
        List<Edit> merged = new ArrayList<>();
        for (int i = 0; i < edits.size(); i++) {
            merged.add(edits.get(i).merge(compound.edits.get(0)));
        }
        return new CompoundEdit(merged, other.occurredOn());
    }


    @Override
    public boolean acrossRows() {
        return edits.size() > 1 || edits.get(0).acrossRows();
    }

}
