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
package com.mammb.code.editor.core;

/**
 * The font metrics test impl.
 * @author Naotsugu Kobayashi
 */
public class FontMetricsTestImpl implements FontMetrics {

    @Override
    public float getMaxAscent() {
        return 0;
    }

    @Override
    public float getAscent() {
        return 0;
    }

    @Override
    public float getXheight() {
        return 0;
    }

    @Override
    public int getBaseline() {
        return 0;
    }

    @Override
    public float getDescent() {
        return 0;
    }

    @Override
    public float getMaxDescent() {
        return 0;
    }

    @Override
    public float getLeading() {
        return 0;
    }

    @Override
    public float getLineHeight() {
        return 2;
    }

    @Override
    public double standardCharWidth() {
        return 1;
    }

    @Override
    public float getAdvance(int codePoint) {
        return codePoint < 1024 ? 1 : 2;
    }

}
