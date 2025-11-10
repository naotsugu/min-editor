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

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * AutoFill.
 * @author Naotsugu Kobayashi
 */
public class AutoFill {

    private FillStrategy fillStrategy;
    private int n;

    public String apply(String string) {
        if (n == 0) {
            fillStrategy = FillStrategy.of(string);
        } else if (n == 1) {
            fillStrategy = FillStrategy.of(fillStrategy, string);
        }
        return fillStrategy.at(n++, string);
    }

    interface FillStrategy {

        String at(int n, String value);

        static FillStrategy of(String value) {
            if (value == null || value.isBlank()) {
                return new IntIncrement(1, 1);
            } else {
                OptionalInt maybeInt = asInt(value);
                if (maybeInt.isPresent()) {
                    return new IntIncrement(maybeInt.getAsInt(), 1);
                }
                OptionalDouble maybeDouble = asDouble(value);
                if (maybeDouble.isPresent()) {
                    return new DoubleIncrement(maybeDouble.getAsDouble(), 1);
                }
                return new Passthrough();
            }
        }
        static FillStrategy of(FillStrategy current, String value) {
            return switch (current) {
                case IntIncrement(var initial, _) -> {
                    OptionalInt maybeInt = asInt(value);
                    if (maybeInt.isPresent()) {
                        yield new IntIncrement(initial, maybeInt.getAsInt() - initial);
                    }
                    OptionalDouble maybeDouble = asDouble(value);
                    if (maybeDouble.isPresent()) {
                        yield new DoubleIncrement(initial, maybeDouble.getAsDouble() - initial);
                    }
                    yield current;
                }
                case DoubleIncrement(var initial, _) -> {
                    OptionalDouble maybeDouble = asDouble(value);
                    if (maybeDouble.isPresent()) {
                        yield new DoubleIncrement(initial, maybeDouble.getAsDouble() - initial);
                    }
                    yield current;
                }
                default -> current;
            };
        }
    }

    record Passthrough() implements FillStrategy {
        @Override
        public String at(int n, String value) {
            return value;
        }
    }

    record IntIncrement(int initial, int distance) implements FillStrategy {
        @Override
        public String at(int n, String value) {
            return String.valueOf(initial + n * distance);
        }
    }

    record DoubleIncrement(double initial, double distance) implements FillStrategy {
        @Override
        public String at(int n, String value) {
            return String.valueOf(initial + n * distance);
        }
    }

    private static OptionalInt asInt(String string) {
        try {
            return OptionalInt.of(Integer.parseInt(string));
        } catch (NumberFormatException _) { }
        return OptionalInt.empty();
    }

    private static OptionalDouble asDouble(String string) {
        try {
            return OptionalDouble.of(Double.parseDouble(string));
        } catch (NumberFormatException _) { }
        return OptionalDouble.empty();
    }

}
