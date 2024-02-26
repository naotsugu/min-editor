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
package com.mammb.code.editor.model.find;

/**
 * The FoundRun.
 * @param chOffset the offset
 * @param cpOffset the code point offset
 * @param length the search result string length
 * @param right
 * @param peripheral
 * @param offsetOnPeripheral
 * @param row the number of row
 * @author Naotsugu Kobayashi
 */
public record FoundRun(
        long chOffset,
        long cpOffset,
        int length,
        boolean right,
        String peripheral,
        int offsetOnPeripheral,
        int row) implements Found {

    /**
     * Get the matched text.
     * @return the matched text
     */
    public String text() {
        int index = Math.toIntExact(chOffset - offsetOnPeripheral);
        return peripheral.substring(index, index + length);
    }

}
