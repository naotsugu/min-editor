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
package com.mammb.code.editor2.model.edit.impl;

import com.mammb.code.editor2.model.edit.EditTo;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.edit.Edit;

/**
 * EmptyEdit.
 * @author Naotsugu Kobayashi
 */
public record EmptyEdit() implements Edit {

    @Override
    public long occurredOn() {
        return 0;
    }

    @Override
    public Edit flip() {
        return this;
    }

    @Override
    public void apply(EditTo editTo) {
    }

    @Override
    public Textual applyTo(Textual textual) {
        return textual;
    }

    @Override
    public boolean isSingleEdit() {
        return true;
    }

}
