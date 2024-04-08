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
package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointRange;
import java.util.Collections;
import java.util.List;

/**
 * OffsetPointRange supplier.
 * @author Naotsugu Kobayashi
 */
public interface RangeSupplier {

    /** The empty range supplier. */
    RangeSupplier empty = Collections::emptyList;


    /**
     * Get the OffsetPointRange list.
     * @return the OffsetPointRange list
     */
    List<OffsetPointRange> getRanges();


    /**
     * Create the fix point range.
     * @param start the start offset point
     * @param end the end offset point
     * @return the fix point range
     */
    static RangeSupplier of(OffsetPoint start, OffsetPoint end) {
        var ranges = List.of(OffsetPointRange.of(start, end));
        return () -> ranges;
    }


    /**
     * Create the open point range.
     * @param point the start offset point
     * @return the open point range
     */
    static OpenRangeSupplier openOf(OffsetPoint point) {
        return new OpenRangeSupplier(point);
    }


    /**
     * The open range supplier.
     */
    class OpenRangeSupplier implements RangeSupplier {

        private final OffsetPoint start;
        private List<OffsetPointRange> ranges = List.of();

        OpenRangeSupplier(OffsetPoint start) {
            this.start = start;
        }

        public void to(OffsetPoint end) {
            ranges = List.of(OffsetPointRange.of(start, end));
        }

        @Override
        public List<OffsetPointRange> getRanges() {
            return ranges;
        }
    }

}
