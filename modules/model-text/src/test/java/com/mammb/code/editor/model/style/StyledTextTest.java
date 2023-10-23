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
package com.mammb.code.editor.model.style;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link StyledText).
 * @author Naotsugu Kobayashi
 */
class StyledTextTest {

    @Test
    void spans() {

        Textual textual = Textual.of(OffsetPoint.zero,"abc123\n");
        StyledText target = StyledText.of(textual);
        // abc123$
        // ***         Color #808080
        //   ***     BgColor #000000
        //    ***    FontSize     20
        // 0123456
        target.putStyle(StyleSpan.of(new Style.Color("#808080"), 0, 3));
        target.putStyle(StyleSpan.of(new Style.BgColor("#000000"), 2, 3));
        target.putStyle(StyleSpan.of(new Style.FontSize(20), 3, 3));

        List<StyledText> styledTexts = target.spans();
        assertEquals(5, styledTexts.size());

        assertEquals("ab", styledTexts.get(0).text());
        assertEquals(1, styledTexts.get(0).styles().size());
        assertEquals(0, styledTexts.get(0).styles().get(0).point());
        assertEquals(2, styledTexts.get(0).styles().get(0).length());

        assertEquals("c", styledTexts.get(1).text());
        assertEquals(2, styledTexts.get(1).styles().size());
        assertEquals(0, styledTexts.get(1).styles().get(0).point());
        assertEquals(1, styledTexts.get(1).styles().get(0).length());
        assertEquals(0, styledTexts.get(1).styles().get(1).point());
        assertEquals(1, styledTexts.get(1).styles().get(1).length());

        assertEquals("12", styledTexts.get(2).text());
        assertEquals(2, styledTexts.get(2).styles().size());
        assertEquals(2, styledTexts.get(2).styles().size());
        assertEquals(0, styledTexts.get(2).styles().get(0).point());
        assertEquals(2, styledTexts.get(2).styles().get(0).length());
        assertEquals(0, styledTexts.get(2).styles().get(1).point());
        assertEquals(2, styledTexts.get(2).styles().get(1).length());

        assertEquals("3", styledTexts.get(3).text());
        assertEquals(1, styledTexts.get(3).styles().size());
        assertEquals(0, styledTexts.get(3).styles().get(0).point());
        assertEquals(1, styledTexts.get(3).styles().get(0).length());

        assertEquals("\n", styledTexts.get(4).text());
        assertEquals(0, styledTexts.get(4).styles().size());
    }

    @Test
    void spansWhenZeroStyles() {

        Textual textual = Textual.of(OffsetPoint.zero,"abc");
        StyledText target = StyledText.of(textual);

        List<StyledText> styledTexts = target.spans();
        assertEquals(1, styledTexts.size());
        assertEquals("abc", styledTexts.get(0).text());
        assertEquals(OffsetPoint.zero, styledTexts.get(0).point());

    }

    @Test
    void spansWhenZeroLength() {

        Textual textual = Textual.of(OffsetPoint.of(1, 5, 5),"");
        StyledText target = StyledText.of(textual);
        target.putStyle(StyleSpan.of(new Style.Context("z"), 0, 0));

        List<StyledText> styledTexts = target.spans();
        assertEquals(1, styledTexts.size());
        assertEquals("", styledTexts.get(0).text());
        assertEquals(textual.point(), styledTexts.get(0).point());

    }

}
