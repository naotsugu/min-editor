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
package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.content.Content;
import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.until.Until;

/**
 * ContentAdapter.
 * @author Naotsugu Kobayashi
 */
public record RowAdapter(Content content) implements RowSupplier {

    /** The empty string. */
    private static final String empty = "";


    @Override
    public String at(long cpOffset) {
        byte[] bytes = content.bytes(cpOffset, Until.lfInclusive());
        return (bytes.length == 0) ? empty : new String(bytes, content.charset());
    }


    @Override
    public String before(long cpOffset) {
        byte[] bytes = content.bytesBefore(cpOffset, Until.lf(2));
        return (bytes.length == 0) ? empty : new String( bytes, content.charset());
    }


    @Override
    public long offset(long startCpOffset, Until<byte[]> until) {
        return content.offset(startCpOffset, until);
    }


    @Override
    public long offsetBefore(long startCpOffset, Until<byte[]> until) {
        return content.offsetBefore(startCpOffset, until);
    }

    @Override
    public long cpLength() {
        return content().length();
    }

}
