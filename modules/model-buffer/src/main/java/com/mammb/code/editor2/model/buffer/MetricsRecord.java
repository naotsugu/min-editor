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
package com.mammb.code.editor2.model.buffer;

import java.nio.file.Path;

/**
 * MetricsRecord.
 * @param path the content path
 * @param byteLen the byte length of content
 * @param cpCount the code point count
 * @param chCount the char count
 * @param invalidCpCount the invalid code point count
 * @param crCount the carriage return count
 * @param lfCount the line feed count
 * @param isDirty dirty or not
 * @author Naotsugu Kobayashi
 */
public record MetricsRecord(
        Path path,
        long byteLen,
        int cpCount,
        int chCount,
        int invalidCpCount,
        int crCount,
        int lfCount,
        boolean isDirty) implements Metrics {

    /**
     * Create a new Metrics by given metrics.
     * @param m the source metrics
     */
    public MetricsRecord(Metrics m) {
        this(m.path(), m.byteLen(), m.cpCount(), m.chCount(), m.invalidCpCount(), m.crCount(), m.lfCount(), m.isDirty());
    }

}
