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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.model.layout.Layout;
import com.mammb.code.editor.model.layout.Span;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.layout.TextRun;
import java.util.function.Function;

/**
 * LayoutRun.
 * @author Naotsugu Kobayashi
 */
public record LayoutRun(TextRun run, double offsetY) implements TextRun {

    public double x() {
        return run.layout().x();
    }

    public double y() {
        return run.layout().y() + offsetY();
    }

    @Override
    public Layout layout() {
        return run.layout();
    }

    @Override
    public int start() {
        return run.start();
    }

    @Override
    public int length() {
        return run.length();
    }

    @Override
    public Function<Integer, Float> offsetToX() {
        return run.offsetToX();
    }

    @Override
    public Function<Double, Integer> xToOffset() {
        return run.xToOffset();
    }

    @Override
    public TextLine textLine() {
        return run.textLine();
    }

    @Override
    public Span source() {
        return run.source();
    }

}
