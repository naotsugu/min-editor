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
package com.mammb.code.editor2.model.style.impl;

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyleSpan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link StyledSubText}.
 * @author Naotsugu Kobayashi
 */
class StyledSubTextTest {

    @Test void test() {

        var text = PointText.of(OffsetPoint.zero, "0123456789");
        StyledTextRecord styled = new StyledTextRecord(text);

        //    * * *             font size 1
        //        * *           font size 2
        //          * *         font size 3
        //              * * *   font size 4
        //      * * * * * * *   font size 5
        // |0|1|2|3|4|5|6|7|8|9|
        //          ^ ^ ^ ^
        styled.putStyle(StyleSpan.of(new Style.FontSize(1), 1, 3));
        styled.putStyle(StyleSpan.of(new Style.FontSize(2), 3, 2));
        styled.putStyle(StyleSpan.of(new Style.FontSize(3), 4, 2));
        styled.putStyle(StyleSpan.of(new Style.FontSize(4), 6, 3));
        styled.putStyle(StyleSpan.of(new Style.FontSize(5), 2, 7));

        StyledSubText sub = StyledSubText.of(styled, 4, 4);
        assertEquals("4567", sub.text());

        //          *           font size 2
        //          * *         font size 3
        //              * *     font size 4
        //          * * * *     font size 5
        //          0 1 2 3
        //         |4|5|6|7|
        //          ^ ^ ^ ^
        assertEquals(StyleSpan.of(new Style.FontSize(2), 0, 1), sub.styles().get(0));
        assertEquals(StyleSpan.of(new Style.FontSize(3), 0, 2), sub.styles().get(1));
        assertEquals(StyleSpan.of(new Style.FontSize(4), 2, 2), sub.styles().get(2));
        assertEquals(StyleSpan.of(new Style.FontSize(5), 0, 4), sub.styles().get(3));
    }

}

