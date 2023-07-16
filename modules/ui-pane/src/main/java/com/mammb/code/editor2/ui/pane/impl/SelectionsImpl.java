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
package com.mammb.code.editor2.ui.pane.impl;

import com.mammb.code.editor2.ui.pane.Selection;
import com.mammb.code.editor2.ui.pane.Selections;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * SelectionsImpl.
 * @author Naotsugu Kobayashi
 */
public class SelectionsImpl implements Selections {

    private final List<Selection> list = new ArrayList<>();

    @Override
    public void put(Selection selection) {
        list.add(selection);
    }

    @Override
    public Selection put(int offset) {
        Selection sel = new SelectionImpl(offset);
        list.add(sel);
        return sel;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public void forEach(Consumer<? super Selection> action) {
        list.forEach(action);
    }

}
