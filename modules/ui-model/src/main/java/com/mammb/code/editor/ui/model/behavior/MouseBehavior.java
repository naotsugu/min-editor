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
package com.mammb.code.editor.ui.model.behavior;

import com.mammb.code.editor.ui.model.impl.Editor;

/**
 * MouseBehavior.
 * @author Naotsugu Kobayashi
 */
public class MouseBehavior {

    public static void click(Editor self, double x, double y) {
        if (self.selection().isDragging()) {
            self.selection().to(self.caret().caretPoint());
            self.selection().endDragging();
            return;
        }
        self.selection().clear();
        double textLeft = self.screen().gutter().width() - self.hScroll().getValue();
        int offset = self.texts().at(x - textLeft, y);
        self.caret().at(offset, true);
    }

    public static void clickDouble(Editor self, double x, double y) {
        double textLeft = self.screen().gutter().width() - self.hScroll().getValue();
        int[] offsets = self.texts().atAroundWord(x - textLeft, y);
        if (offsets.length == 2) {
            SelectBehavior.select(offsets, self.caret(), self.selection());
        } else {
            self.caret().at(offsets[0], true);
        }
    }

    public static void dragged(Editor self, double x, double y) {
        double textLeft = self.screen().gutter().width() - self.hScroll().getValue();
        self.caret().at(self.texts().at(x - textLeft, y), true);
        if (self.selection().isDragging()) {
            self.selection().to(self.caret().caretPoint());
        } else {
            self.selection().startDragging(self.caret().caretPoint());
        }
    }

}
