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
package com.mammb.code.editor.model.text;

/**
 * OffsetPointRange.
 * @author Naotsugu Kobayashi
 */
public interface OffsetPointRange {

    /**
     * Get the start offset point.
     * @return the start offset point
     */
    OffsetPoint startOffsetPoint();

    /**
     * Get the end offset point.
     * @return the end offset point
     */
    OffsetPoint endOffsetPoint();

    /**
     * Get the min offset point.
     * @return the min offset point
     */
    default OffsetPoint minOffsetPoint() {
        OffsetPoint start = startOffsetPoint();
        OffsetPoint end   = endOffsetPoint();
        if (start == null && end == null) {
            return null;
        } else if (start == null) {
            return end;
        } else if (end == null) {
            return start;
        } else {
            return start.compareTo(end) <= 0 ? start : end;
        }
    }

    /**
     * Get the max offset point.
     * @return the max offset point
     */
    default OffsetPoint maxOffsetPoint() {
        OffsetPoint start = startOffsetPoint();
        OffsetPoint end   = endOffsetPoint();
        if (start == null && end == null) {
            return null;
        } else if (start == null) {
            return end;
        } else if (end == null) {
            return start;
        } else {
            return start.compareTo(end) <= 0 ? end :start;
        }
    }

    /**
     * Get the char length of range.
     * @return the char length of range
     */
    default long length() {
        OffsetPoint min = minOffsetPoint();
        OffsetPoint max = maxOffsetPoint();
        return (min == null || max == null) ? 0 : max.offset() - min.offset();
    }

    /**
     * Get the code point length of range.
     * @return the code point length of range
     */
    default long cpLength() {
        OffsetPoint min = minOffsetPoint();
        OffsetPoint max = maxOffsetPoint();
        return (min == null || max == null) ? 0 : max.cpOffset() - min.cpOffset();
    }

    /**
     * Create a new OffsetPointRange.
     * @param startOffsetPoint the start offset point
     * @param endOffsetPoint the end offset point
     * @return a new OffsetPointRange
     */
    static OffsetPointRange of(OffsetPoint startOffsetPoint, OffsetPoint endOffsetPoint) {
        record OffsetPointRangeRecord(OffsetPoint startOffsetPoint, OffsetPoint endOffsetPoint) implements OffsetPointRange { }
        return new OffsetPointRangeRecord(startOffsetPoint, endOffsetPoint);
    }

}
