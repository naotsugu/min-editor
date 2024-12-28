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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Action;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * The application command.
 * @author Naotsugu Kobayashi
 */
public sealed interface Command {

    /** The logger. */
    System.Logger log = System.getLogger(Command.class.getName());

    interface Hidden { }
    interface RequireArgs { }
    interface RequireArgs1<T> extends RequireArgs { }

    record ActionCommand(Action action) implements Command {}
    record OpenChoose() implements Command {}
    record Save() implements Command {}
    record SaveAs() implements Command {}
    record New() implements Command {}
    record Config() implements Command {}
    record Palette(Class<? extends Command> initial) implements Command, Hidden {}
    record Empty() implements Command, Hidden {}

    record ToLowerCase() implements Command {}
    record ToUpperCase() implements Command {}
    record Sort() implements Command {}
    record Unique() implements Command {}
    record Calc() implements Command {}
    record Pwd() implements Command {}
    record Pwf() implements Command {}
    record Today() implements Command {}
    record Now() implements Command {}
    // TODO convert dec to hex to bin to ..
    // TODO find next, find prev

    record FindAll(String str) implements Command, RequireArgs1<String> { }
    record GoTo(Integer rowNumber) implements Command, RequireArgs1<Integer> { }
    record Filter(String str) implements Command, RequireArgs1<String> { }
    record Wrap(Integer width) implements Command, RequireArgs1<Integer> { }
    record Open(String path) implements Command, RequireArgs1<String> { }

    static String promptText(Class<? extends Command> clazz) {
        if (FindAll.class.isAssignableFrom(clazz)) {
            return "enter a string to search";
        }
        if (GoTo.class.isAssignableFrom(clazz)) {
            return "enter a line number";
        }
        if (Filter.class.isAssignableFrom(clazz)) {
            return "enter a regexp string to filter";
        }
        if (Wrap.class.isAssignableFrom(clazz)) {
            return "enter a wrap width(number of characters)";
        }
        return "";
    }

    static Map<String, Class<? extends Command>> values() {
        @SuppressWarnings("unchecked")
        var permitted = (Class<? extends Command>[]) Command.class.getPermittedSubclasses();
        return Arrays.stream(permitted)
            .filter(not(Hidden.class::isAssignableFrom))
            .collect(Collectors.toMap(Class::getSimpleName, UnaryOperator.identity(), (e1, e2) -> e2));
    }

    static Command newInstance(Class<? extends Command> clazz, String... args) {
        if (!RequireArgs.class.isAssignableFrom(clazz)) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
            return new Empty();
        }

        if (RequireArgs1.class.isAssignableFrom(clazz) && args.length > 0) {
            try {
                Class<?> argType = Arrays.stream(clazz.getGenericInterfaces())
                    .filter(ParameterizedType.class::isInstance)
                    .map(ParameterizedType.class::cast)
                    .map(type -> type.getActualTypeArguments()[0])
                    .filter(Class.class::isInstance)
                    .map(Class.class::cast)
                    .findFirst().get();
                Object argObj = argType.getMethod("valueOf", String.class).invoke(null, args[0]);
                Constructor<? extends Command> constructor = clazz.getConstructor(argType);
                return constructor.newInstance(argType.cast(argObj));
            } catch (Exception ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }
        return new Empty();
    }

}
