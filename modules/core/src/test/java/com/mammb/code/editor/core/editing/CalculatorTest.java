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
package com.mammb.code.editor.core.editing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link Calculator}.
 * @author Naotsugu Kobayashi
 */
class CalculatorTest {

    @Test
    void infixToRpn() {

        assertEquals("3 4 + 1 2 - *",
            String.join(" ", Calculator.infixToRpn("( 3 + 4 ) * ( 1 - 2 )")));

        assertEquals("3 4 2 * 1 5 - 2 3 ^ ^ / +",
            String.join(" ", Calculator.infixToRpn("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3")));

        assertEquals("1 2 + 3 4 / 5 6 + ^ *",
            String.join(" ", Calculator.infixToRpn("( 1 + 2 ) * ( 3 / 4 ) ^ ( 5 + 6 )")));

        assertEquals("5 2 ^ 2 5 * 8 * + 8 2 ^ +",
            String.join(" ", Calculator.infixToRpn("5 ^ 2 + 2 * 5 * 8 + 8 ^ 2")));

        assertEquals("5 ~ 3 + 8 *",
            String.join(" ", Calculator.infixToRpn("( ( -5 + 3 ) * 8)")));

        assertEquals("2e3 4 *",
            String.join(" ", Calculator.infixToRpn("2e3 * 4")));

        assertEquals("5 8 ~ *",
            String.join(" ", Calculator.infixToRpn("5 * - 8")));

        assertEquals("2 ~ 3 ^",
            String.join(" ", Calculator.infixToRpn("- 2 ^ 3")));

    }

    @Test
    void calc() {
        assertEquals("3", Calculator.calc("1+2"));
        assertEquals("7", Calculator.calc("1+2*3"));
        assertEquals("2", Calculator.calc("10/5"));
        assertEquals("8", Calculator.calc("2^3"));

        assertEquals("14", Calculator.calc("2+3*4"));
        assertEquals("20", Calculator.calc("(2+3)*4"));
        assertEquals("144", Calculator.calc("(2+10)^2"));

        assertEquals("2.5", Calculator.calc("10/4"));
        assertEquals("10.5", Calculator.calc("5.5+5"));
        assertEquals("3.14", Calculator.calc("1.1+2.04"));

        assertEquals("-8", Calculator.calc("-2^3"));
        assertEquals("4", Calculator.calc("(-2)^2"));
        assertEquals("-4", Calculator.calc("-(2^2)"));

        assertEquals("25", Calculator.calc("((2+3)*5/5)^2"));
    }

    @Test
    void calcScientificNotation() {
        assertEquals("2000", Calculator.calc("2e3"));
        assertEquals("2100", Calculator.calc("2.1e3"));
    }

    @Test
    void calcDivisionByZero() {
        assertEquals("NaN", Calculator.calc("1/0"));
    }

}

