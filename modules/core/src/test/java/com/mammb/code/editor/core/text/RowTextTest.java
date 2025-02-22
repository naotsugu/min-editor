/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.text;

import com.mammb.code.editor.core.FontMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link RowText}.
 * @author Naotsugu Kobayashi
 */
class RowTextTest {

    @Test
    void of() {
        var text = RowText.of(0, "aあb", new FontMetricsImpl());
        assertEquals(0, text.row());
        assertEquals(4, text.width());
        assertArrayEquals(new double[] { 1, 2, 1 }, text.advances());
    }

    @Test
    void ofWithTab() {
        var text = RowText.of(0, "\ta", new FontMetricsImpl());
        assertArrayEquals(new double[] { 4, 1 }, text.advances());

        text = RowText.of(0, "a\t", new FontMetricsImpl());
        assertArrayEquals(new double[] { 1, 3 }, text.advances());

        text = RowText.of(0, "aa\t", new FontMetricsImpl());
        assertArrayEquals(new double[] { 1, 1, 2 }, text.advances());

        text = RowText.of(0, "aaa\t", new FontMetricsImpl());
        assertArrayEquals(new double[] { 1, 1, 1, 1 }, text.advances());

        text = RowText.of(0, "aaaa\t", new FontMetricsImpl());
        assertArrayEquals(new double[] { 1, 1, 1, 1, 4 }, text.advances());

        text = RowText.of(0, "aa\ta\t", new FontMetricsImpl());
        assertArrayEquals(new double[] { 1, 1, 2, 1, 3 }, text.advances());
    }

    private static class FontMetricsImpl implements FontMetrics {
        @Override public float getMaxAscent() { return 0; }
        @Override public float getAscent() { return 0; }
        @Override public float getXheight() { return 0; }
        @Override public int getBaseline() { return 0; }
        @Override public float getDescent() { return 0; }
        @Override public float getMaxDescent() { return 0; }
        @Override public float getLeading() { return 0; }
        @Override public float getLineHeight() { return 2; }
        @Override public double standardCharWidth() { return 1; }
        @Override public float getAdvance(int codePoint) { return codePoint < 1024 ? 1 : 2; }
    }

}
