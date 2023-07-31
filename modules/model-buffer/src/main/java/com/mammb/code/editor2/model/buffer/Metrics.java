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

import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.text.LineEnding;

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

    long byteLen();

    int cpCount();

    int chCount();

    int invalidCpCount();

    int crCount();

    int lfCount();

    void apply(Edit edit);

    void setPath(Path pate);

    /**
     * Get the line ending.
     * @return the line ending
     */
    default LineEnding lineEnding() {
        if (crCount() == 0 && lfCount() == 0) {
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
