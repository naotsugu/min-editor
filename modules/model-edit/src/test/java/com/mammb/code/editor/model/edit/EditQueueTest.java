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
package com.mammb.code.editor.model.edit;

import com.mammb.code.editor.model.edit.impl.InsertEdit;
import com.mammb.code.editor.model.text.OffsetPoint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link EditQueue}.
 * @author Naotsugu Kobayashi
 */
class EditQueueTest {

    @Test void peekAll() {
        var queue = EditQueue.of();
        queue.push(Edit.insert("abc", OffsetPoint.zero));

        var sb = new StringBuilder();
        queue.peekEach(e -> sb.append(((InsertEdit)e).text()));
        assertEquals("abc", sb.toString());
    }

}
