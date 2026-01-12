/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Caret;
import com.mammb.code.editor.core.CaretAlterGroup;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The CaretAlterGroup.
 * @author Naotsugu Kobayashi
 */
public class CaretAlterGroupImpl implements CaretAlterGroup {

    private List<Deque<Caret>> stacks;

    private boolean isDown;

    private CaretAlterGroupImpl(List<Caret> carets, boolean isDown) {
        this.stacks = carets.stream()
            .map(c -> new ArrayDeque<>(List.of(c)))
            .collect(Collectors.toList());
        this.isDown = isDown;
    }

    static CaretAlterGroupImpl down(List<Caret> carets) {
        return new CaretAlterGroupImpl(carets, true);
    }

    static CaretAlterGroupImpl up(List<Caret> carets) {
        return new CaretAlterGroupImpl(carets, false);
    }


}
