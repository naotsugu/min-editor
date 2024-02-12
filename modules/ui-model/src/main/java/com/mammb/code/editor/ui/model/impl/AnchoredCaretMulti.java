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

import com.mammb.code.editor.model.text.OffsetPointRange;
import com.mammb.code.editor.ui.model.RangeSupplier;
import java.util.ArrayList;
import java.util.List;

/**
 * AnchoredCaretMulti.
 * @author Naotsugu Kobayashi
 */
public class AnchoredCaretMulti implements RangeSupplier {

    /** The main AnchoredCaret. */
    private final AnchoredCaret main;

    /** The AnchoredCaret. */
    private final List<AnchoredCaret> list = new ArrayList<>();


    /**
     * Constructor.
     * @param main the main CaretLine
     * @param moons the moons CaretLines
     */
    public AnchoredCaretMulti(CaretLine main, List<CaretLine> moons) {
        this.main = new AnchoredCaret(main);
        this.list.add(this.main);
        moons.forEach(c -> this.list.add(new AnchoredCaret(c)));
    }


    @Override
    public List<OffsetPointRange> getRanges() {
        return list.stream().map(AnchoredCaret::offsetPointRange).toList();
    }

}
