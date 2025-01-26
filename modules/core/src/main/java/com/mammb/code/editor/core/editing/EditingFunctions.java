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

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The editing functions.
 * @author Naotsugu Kobayashi
 */
public interface EditingFunctions {

    /**
     * logger.
     */
    System.Logger log = System.getLogger(EditingFunctions.class.getName());

    /**
     * Pass through function.
     */
    Function<String, String> passThrough = text -> text;

    /**
     * To lower case function.
     */
    Function<String, String> toLower = String::toLowerCase;
    /**
     * To upper case function.
     */
    Function<String, String> toUpper = String::toUpperCase;

    /**
     * Indent function.
     */
    Function<String, String> indent = text -> Arrays.stream(text.split("(?<=\\n)"))
        .map(s -> " ".repeat(4) + s)
        .collect(Collectors.joining());
    /**
     * Un indent function.
     */
    Function<String, String> unindent = text -> Arrays.stream(text.split("(?<=\\n)"))
        .map(s -> s.replaceFirst("^ {4}?|^\t", ""))
        .collect(Collectors.joining());

    /**
     * Sort function.
     */
    Function<String, String> sort = text -> Arrays.stream(text.split("(?<=\\n)"))
        .sorted().collect(Collectors.joining());

    /**
     * Unique function.
     */
    Function<String, String> unique = text -> Arrays.stream(text.split("(?<=\\n)"))
        .distinct().collect(Collectors.joining());

    /**
     * Calc function.
     */
    Function<String, String> toCalc = text -> {
        // if it contains an equal sign, delete the rest
        int eq = text.indexOf('=');
        String formula = (eq > 1) ? text.substring(eq) : text;

        try {
            String s = text.contains(" ") ? " " : "";
            return "%s%s=%s%s".formatted(text, s, s, Calculator.calc(text));
        } catch (Exception ignore) {
            log.log(System.Logger.Level.WARNING, ignore);
        }
        return text;
    };

    Function<String, String> decToHex = text -> hex(text, 10);
    Function<String, String> decToBin = text -> bin(text, 10);
    Function<String, String> hexToBin = text -> bin(text.toLowerCase().startsWith("0x") ? text.substring(2) : text, 16);
    Function<String, String> hexToDec = text -> dec(text.toLowerCase().startsWith("0x") ? text.substring(2) : text, 16);
    Function<String, String> binToHex = text -> hex(text.replaceAll(" ", ""), 2);
    Function<String, String> binToDec = text -> dec(text.replaceAll(" ", ""), 2);

    private static String bin(String text, int radix) {
        try {
            String bin = Integer.toBinaryString(Integer.parseInt(text.toLowerCase(), radix));
            bin = "0".repeat((bin.length() > 4) ? (4 - bin.length() % 4) : (4 - bin.length())) + bin;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bin.length(); i++) {
                if (i != 0 && (i % 4) == 0) sb.append(" ");
                sb.append(bin.charAt(i));
            }
            return sb.toString();
        } catch (Exception ignore) {
            return text;
        }
    }

    private static String hex(String text, int radix) {
        try {
            return "0x" + Integer.toHexString(Integer.parseInt(text.toLowerCase(), radix));
        } catch (Exception ignore) {
            return text;
        }
    }

    private static String dec(String text, int radix) {
        try {
            return Integer.toString(Integer.parseInt(text.toLowerCase(), radix));
        } catch (Exception ignore) {
            return text;
        }
    }

}
