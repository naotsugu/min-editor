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
package com.mammb.code.editor.ui.app;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link AddressPath}.
 * @author Naotsugu Kobayashi
 */
class AddressPathTest {

    @Test
    void foo() {
        var target = new AddressPath(Path.of("/aa/bb/cc/dd.java"), false);
        assertEquals("/", target.dirAt(0).toString());          // |/ a a / b b / c c / d d . java
        assertEquals("/", target.dirAt(1).toString());          //  /|a a / b b / c c / d d . java
        assertEquals("/", target.dirAt(2).toString());          //  / a|a / b b / c c / d d . java
        assertEquals("/", target.dirAt(3).toString());          //  / a a|/ b b / c c / d d . java

        assertEquals("/aa", target.dirAt(4).toString());        //  / a a /|b b / c c / d d . java
        assertEquals("/aa", target.dirAt(5).toString());        //  / a a / b|b / c c / d d . java
        assertEquals("/aa", target.dirAt(6).toString());        //  / a a / b b|/ c c / d d . java

        assertEquals("/aa/bb", target.dirAt(7).toString());     //  / a a / b b /|c c / d d . java
        assertEquals("/aa/bb", target.dirAt(8).toString());     //  / a a / b b / c|c / d d . java
        assertEquals("/aa/bb", target.dirAt(9).toString());     //  / a a / b b / c c|/ d d . java

        assertEquals("/aa/bb/cc", target.dirAt(10).toString()); //  / a a / b b / c c /|d d . java
        assertEquals("/aa/bb/cc", target.dirAt(11).toString()); //  / a a / b b / c c / d|d . java
        assertEquals("/aa/bb/cc", target.dirAt(12).toString()); //  / a a / b b / c c / d d|. java
    }
}
