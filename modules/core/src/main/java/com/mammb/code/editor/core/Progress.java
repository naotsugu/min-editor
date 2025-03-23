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
package com.mammb.code.editor.core;

/**
 * The progress.
 * @author Naotsugu Kobayashi
 */
public interface Progress {

    /**
     * A value from Double.MIN_VALUE up to max.
     * If the value is greater than max, then it will be clamped at max.
     * If the value passed is negative, or Infinity, or NaN, then the resulting percentDone will be -1 (thus, indeterminate).
     * @return the up to max value
     */
    double workDone();

    /**
     * A value from Double.MIN_VALUE to Double.MAX_VALUE.
     * Infinity and NaN are treated as -1.
     * @return the max value
     */
    double max();

    /**
     * Create a next workDone progress.
     * @param workDone the up to max value
     * @return a new {@link Progress}
     */
    default Progress workDone(double workDone) {
        return of(workDone, max());
    }

    /**
     * Create a new {@link Progress}.
     * @param workDone the up to max value
     * @param max the max value
     * @return a new {@link Progress}
     */
    static Progress of(double workDone, double max) {
        record ProgressRecord(double workDone, double max) implements Progress { }
        return new ProgressRecord(workDone, max);
    }

}
