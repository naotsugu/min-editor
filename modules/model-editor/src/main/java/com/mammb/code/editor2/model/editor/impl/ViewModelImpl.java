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
package com.mammb.code.editor2.model.editor.impl;

import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.editor.Selection;
import com.mammb.code.editor2.model.editor.ViewModel;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

import java.util.List;

/**
 * ViewModel.
 * @author Naotsugu Kobayashi
 */
public class ViewModelImpl implements ViewModel {

    private TextBuffer<? extends Textual> buffer;

    private OffsetPoint caret;

    private List<Selection> selections;

    public ViewModelImpl() {
    }


    @Override
    public OffsetPoint caret() {
        return caret;
    }

    @Override
    public List<Selection> selections() {
        return selections;
    }

}
