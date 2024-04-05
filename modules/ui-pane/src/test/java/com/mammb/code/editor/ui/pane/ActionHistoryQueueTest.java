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
package com.mammb.code.editor.ui.pane;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test of {@link ActionHistoryQueue}.
 * @author Naotsugu Kobayashi
 */
class ActionHistoryQueueTest {

    @Test
    void repetition() {
        var q = new ActionHistoryQueue();
        q.offer(ActionHistory.of(Keys.Action.CARET_DOWN));
        q.offer(ActionHistory.of(Keys.Action.CARET_RIGHT));
        q.offer(ActionHistory.of(Keys.Action.CARET_RIGHT));
        q.offer(ActionHistory.of(Keys.Action.CARET_RIGHT));
        q.offer(ActionHistory.of(Keys.Action.PASTE));
        q.offer(ActionHistory.of(Keys.Action.CARET_DOWN));
        q.offer(ActionHistory.of(Keys.Action.CARET_RIGHT));
        q.offer(ActionHistory.of(Keys.Action.CARET_RIGHT));
        q.offer(ActionHistory.of(Keys.Action.CARET_RIGHT));
        q.offer(ActionHistory.of(Keys.Action.PASTE));

        var list = q.repetition();
        assertEquals(5, list.size());
        assertEquals(Keys.Action.CARET_DOWN, list.get(0).type());
        assertEquals(Keys.Action.CARET_RIGHT, list.get(1).type());
        assertEquals(Keys.Action.CARET_RIGHT, list.get(2).type());
        assertEquals(Keys.Action.CARET_RIGHT, list.get(3).type());
        assertEquals(Keys.Action.PASTE, list.get(4).type());
    }

}
