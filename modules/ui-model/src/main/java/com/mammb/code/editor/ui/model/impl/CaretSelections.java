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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.ui.model.SelectionDraw;
import javafx.scene.canvas.GraphicsContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CaretSelections.
 * @author Naotsugu Kobayashi
 */
public class CaretSelections implements SelectionDraw {

    private final List<CaretSelection> carets;


    public CaretSelections(List<CaretSelection> carets) {
        this.carets = carets;
    }


    public static CaretSelections of(List<CaretLine> caretLines) {
        return new CaretSelections(caretLines.stream()
            .map(CaretSelection::new)
            .collect(Collectors.toList()));
    }

    @Override
    public void draw(GraphicsContext gc, TextRun run, double offsetY, double left) {
        carets.removeIf(CaretSelection::isInvalid);
        carets.forEach(c -> c.draw(gc, run, offsetY, left));
    }


    public void clear() {
        carets.clear();
    }


}
