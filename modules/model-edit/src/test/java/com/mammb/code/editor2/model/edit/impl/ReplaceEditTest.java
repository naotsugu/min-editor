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
package com.mammb.code.editor2.model.edit.impl;

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.core.PointText;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link ReplaceEdit}.
 * @author Naotsugu Kobayashi
 */
class ReplaceEditTest {

    @Test void testAffectTranslateHeadReplace() {

        // 0: | 0 | 1 |
        // 1: |@2 | 3 | 4 |
        // 2: | 5 | 6 |
        List<PointText> lines = pointTexts("""
            0
            23
            56""");

        var edit = new ReplaceEdit(OffsetPoint.of(1, 2, 2), "23", "x", 0);

        assertEquals(lines.get(0), edit.affectTranslate(lines.get(0)));
        assertEquals(pointText(1, 2, 2, "x\n"), edit.affectTranslate(lines.get(1)));
        assertEquals(pointText(2, 4, 4, "56"), edit.affectTranslate(lines.get(2)));
    }

    @Test void testAffectTranslateMidReplace() {

        // 0: | 0 | 1 |
        // 1: | 2 |@3 | 4 |
        // 2: | 5 | 6 |
        List<PointText> lines = pointTexts("""
            0
            23
            56""");

        var edit = new ReplaceEdit(OffsetPoint.of(1, 3, 3), "3", "x", 0);

        assertEquals(lines.get(0), edit.affectTranslate(lines.get(0)));
        assertEquals(pointText(1, 2, 2, "2x\n"), edit.affectTranslate(lines.get(1)));
        assertEquals(pointText(2, 5, 5, "56"), edit.affectTranslate(lines.get(2)));
    }


    private static List<PointText> pointTexts(String string) {
        String[] lines = string.split("(?<=\n)");
        List<PointText> ret = new ArrayList<>();
        for (int i = 0, offset = 0, cpOffset = 0; i < lines.length; i++) {
            String line = lines[i];
            ret.add(PointText.of(OffsetPoint.of(i, offset, cpOffset), line));
            offset += line.length();
            cpOffset += Character.codePointCount(line, 0, line.length());
        }
        return ret;
    }

    private static PointText pointText(int row, int offset, int cpOffset, String text) {
        return PointText.of(OffsetPoint.of(row, offset, cpOffset), text);
    }

}
