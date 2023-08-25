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

import com.mammb.code.editor2.ui.pane.Caret;
import com.mammb.code.editor2.ui.pane.Selection;

/**
 * Select behavior collection.
 * @author Naotsugu Kobayashi
 */
public class SelectBehaviors {

    public static void select(int[] offsetRange, Caret caret, Selection selection) {
        select(offsetRange[0], offsetRange[1], caret, selection);
    }

    public static void select(int offset1, int offset2, Caret caret, Selection selection) {
        int start = Math.min(offset1, offset2);
        int end   = Math.max(offset1, offset2);
        caret.at(start, true);
        selection.start(caret.offsetPoint());
        caret.at(end, true);
        selection.to(caret.offsetPoint());
    }

}
