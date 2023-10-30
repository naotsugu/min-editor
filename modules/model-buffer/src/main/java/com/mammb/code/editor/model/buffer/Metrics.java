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
package com.mammb.code.editor.model.buffer;

import com.mammb.code.editor.model.text.LineEnding;
import com.mammb.code.editor.model.text.OffsetPoint;

import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.lang.System.Logger.Level.WARNING;

/**
 * Metrics.
 * @author Naotsugu Kobayashi
 */
public interface Metrics {

    /** logger. */
    System.Logger log = System.getLogger(Metrics.class.getName());


    /**
     * Get the content path.
     * @return the content path
     */
    Path path();

    /**
     * Get the charset.
     * @return the charset
     */
    Charset charset();

    /**
     * Get the byte length of content.
     * @return the byte length of content
     */
    long byteLen();

    /**
     * Get the code point count.
     * @return the code point count
     */
    int cpCount();

    /**
     * Get the char count.
     * @return the char count
     */
    int chCount();

    /**
     * Get the invalid code point count.
     * @return the invalid code point count
     */
    int invalidCpCount();

    /**
     * Get the carriage return count.
     * @return the carriage return count
     */
    int crCount();

    /**
     * Get the line feed count.
     * @return the line feed count
     */
    int lfCount();

    /**
     * Get whether modified.
     * @return whether modified
     */
    boolean modified();

    /**
     * Get the nearest anchor to the specified line.
     * @param row the specified row
     * @return the nearest anchor
     */
    OffsetPoint anchorPoint(int row);

    /**
     * Add the metrics change listener.
     * @param listener the metrics change listener
     */
    void addListener(MetricsChangeListener listener);

    /**
     * Get the total row count.
     * @return the total row count
     */
    default int rowCount() {
        return lfCount() + 1;
    }

    /**
     * Get the line ending.
     * @return the line ending
     */
    default LineEnding lineEnding() {
        if (crCount() <= 0 && lfCount() <= 0) {
            return LineEnding.platform();
        } else if (crCount() == lfCount()) {
            return LineEnding.CRLF;
        } else if (crCount() == 0 && lfCount() > 0) {
            return LineEnding.LF;
        } else {
            log.log(WARNING, "inconsistent line breaks. CR[{0}] LF[{1}]", crCount(), lfCount());
            return LineEnding.platform();
        }
    }

}
