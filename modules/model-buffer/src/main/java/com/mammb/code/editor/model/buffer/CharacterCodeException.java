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
package com.mammb.code.editor.model.buffer;

/**
 * Runtime exception thrown when a character encoding or decoding error occurs.
 * @author Naotsugu Kobayashi
 */
public class CharacterCodeException extends RuntimeException {

    /**
     * Constructs an instance of this class.
     */
    public CharacterCodeException() {
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * @param message the detail message
     */
    public CharacterCodeException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause(A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown)
     */
    public CharacterCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a detail.
     * @param cause the cause(A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown)
     */
    public CharacterCodeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     * @param message the detail message
     * @param cause the cause(A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown)
     * @param enableSuppression whether suppression is enabled or disabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public CharacterCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
