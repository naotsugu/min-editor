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
package com.mammb.code.editor.model.text;

import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.slice.TextualSlice;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link TextualSlice}.
 * @author Naotsugu Kobayashi
 */
class RowSliceTest {

    @Test void of() {
        var slice = TextualSlice.of(5, RowSupplier.stringOf("abc"));
        // TODO add test
    }
}
