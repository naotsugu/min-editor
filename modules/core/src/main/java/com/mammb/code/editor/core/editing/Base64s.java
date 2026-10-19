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
package com.mammb.code.editor.core.editing;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility methods for Base64 encoding and decoding Unicode characters.
 * @author Naotsugu Kobayashi
 */
public class Base64s {

    public static String encode(CharSequence input) {
        byte[] bytesToEncode = input.toString().getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytesToEncode);
    }

    public static String decode(CharSequence input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input.toString());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String urlEncode(CharSequence input) {
        byte[] bytesToEncode = input.toString().getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().encodeToString(bytesToEncode);
    }

    public static String urlDecode(CharSequence input) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(input.toString());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String mimeEncode(CharSequence input) {
        byte[] bytesToEncode = input.toString().getBytes(StandardCharsets.UTF_8);
        return Base64.getMimeEncoder().encodeToString(bytesToEncode);
    }

    public static String mimeDecode(CharSequence input) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(input.toString());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

}
