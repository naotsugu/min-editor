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

import com.mammb.code.editor2.model.layout.LineLayout;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.ui.pane.ImePallet;
import com.mammb.code.editor2.ui.pane.LayoutLine;

import java.util.Objects;

/**
 * ImePalletImpl.
 * @author Naotsugu Kobayashi
 */
public class ImePalletImpl implements ImePallet {

    private OffsetPoint offsetPoint;

    private String fragments = "";

    public ImePalletImpl() {
    }

    @Override
    public void on(OffsetPoint offsetPoint) {
        this.offsetPoint = Objects.requireNonNull(offsetPoint);
    }

    @Override
    public void off() {
        offsetPoint = null;
        fragments = "";
    }

    @Override
    public LayoutLine compose(String text) {
        fragments = text;
        return null;
    }

    @Override
    public boolean enabled() {
        return offsetPoint != null;
    }

}
