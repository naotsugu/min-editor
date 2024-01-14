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
package com.mammb.code.editor.model.buffer;

import com.mammb.code.editor.model.text.OffsetPoint;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * MetricsRecord.
 * @param path the content path
 * @param charset the charset
 * @param byteLen the byte length of content
 * @param cpCount the code point count
 * @param chCount the char count
 * @param invalidCpCount the invalid code point count
 * @param crCount the carriage return count
 * @param lfCount the line feed count
 * @param modified modified or not
 * @author Naotsugu Kobayashi
 */
public record MetricsRecord(
    Path path,
    Charset charset,
    long byteLen,
    long cpCount,
    long chCount,
    long invalidCpCount,
    int crCount,
    int lfCount,
    boolean modified) implements Metrics {

    /**
     * Create a new Metrics by given metrics.
     * @param m the source metrics
     */
    public MetricsRecord(Metrics m) {
        this(
            m.path(),
            m.charset(),
            m.byteLen(),
            m.cpCount(),
            m.chCount(),
            m.invalidCpCount(),
            m.crCount(),
            m.lfCount(),
            m.modified());
    }

    @Override
    public OffsetPoint anchorPoint(int row) {
        return OffsetPoint.zero;
    }

    @Override
    public void addChangeListener(MetricsChangeListener listener) {
        // no-op
    }

    @Override
    public void removeChangeListener(MetricsChangeListener listener) {
        // no-op
    }

}
