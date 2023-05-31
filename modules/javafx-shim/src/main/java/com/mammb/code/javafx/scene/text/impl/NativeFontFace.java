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
package com.mammb.code.javafx.scene.text.impl;

import com.mammb.code.editor.model.layout.FontFace;
import com.sun.javafx.font.PGFont;

/**
 * FontFaceImpl.
 * @author Naotsugu Kobayashi
 */
public class NativeFontFace implements FontFace<PGFont> {

    private final PGFont pear;

    public NativeFontFace(PGFont pear) {
        this.pear = pear;
    }

    public static FontFace<PGFont> of(PGFont font) {
        return new NativeFontFace(font);
    }

    public static FontFace<PGFont> of(Object font) {
        return of((PGFont) font);
    }

    @Override
    public String fullName() {
        return pear.getFullName();
    }


    @Override
    public PGFont font() {
        return pear;
    }

}
