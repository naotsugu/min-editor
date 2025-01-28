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

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import com.mammb.code.editor.core.Action;

import static java.util.function.Predicate.not;
import static javafx.scene.input.KeyCode.F1;

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

    record ActionCommand(Action action) implements Command, Hidden {}

    record OpenChoose() implements Command {}

    record Save() implements Command {}

    record SaveAs() implements Command {}

    record New() implements Command {}

    record TabClose() implements Command {}

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

    record DecToHex() implements Command {}

    record DecToBin() implements Command {}

    record HexToBin() implements Command {}

    record HexToDec() implements Command {}

    record BinToHex() implements Command {}

    record BinToDec() implements Command {}

    record Backward() implements Command {}

    record Forward() implements Command {}

    record ZoomIn() implements Command {}

    record ZoomOut() implements Command {}

    // TODO find next, find prev

    record FindAll(String str) implements Command, RequireArgs1<String> { }

    record GoTo(Integer rowNumber) implements Command, RequireArgs1<Integer> { }

    record Filter(String str) implements Command, RequireArgs1<String> { }

    record WrapLine(Integer width) implements Command, RequireArgs1<Integer> { }

    record Open(String path) implements Command, RequireArgs1<String> { }

    record Help() implements Command {}

    static String promptText(Class<? extends Command> clazz) {
        return switch (clazz) {
            case Class<?> c when c == FindAll.class -> "enter a string to search";
            case Class<?> c when c == GoTo.class -> "enter a line number";
            case Class<?> c when c == Filter.class -> "enter a regexp string to filter";
            case Class<?> c when c == WrapLine.class -> "enter a wrap width(number of characters)";
            case null, default -> "";
        };
    }

    static String noteText(Class<? extends Command> clazz) {
        return switch (clazz) {
            case Class<?> c when c == OpenChoose.class -> "open file selection dialog";
            case Class<?> c when c == Save.class -> "save the current file";
            case Class<?> c when c == SaveAs.class -> "save under a different name";
            case Class<?> c when c == New.class -> "open new tab";
            case Class<?> c when c == TabClose.class -> "close current tab";
            case Class<?> c when c == Config.class -> "open current config";
            case Class<?> c when c == ToLowerCase.class -> "converts the selected text to lower case";
            case Class<?> c when c == ToUpperCase.class -> "converts the selected text to upper case";
            case Class<?> c when c == Sort.class -> "sort the selected lines";
            case Class<?> c when c == Unique.class -> "unique the selected lines";
            case Class<?> c when c == Calc.class -> "calculates the selected expression";
            case Class<?> c when c == Pwd.class -> "print working directory path";
            case Class<?> c when c == Pwf.class -> "print working file path";
            case Class<?> c when c == Today.class -> "insert current date";
            case Class<?> c when c == Now.class -> "insert current time";
            case Class<?> c when c == DecToHex.class -> "converts the selected decimal to hexadecimal";
            case Class<?> c when c == DecToBin.class -> "converts the selected decimal to binary";
            case Class<?> c when c == HexToBin.class -> "converts the selected hexadecimal to binary";
            case Class<?> c when c == HexToDec.class -> "converts the selected hexadecimal to decimal";
            case Class<?> c when c == BinToHex.class -> "converts the selected binary to hexadecimal";
            case Class<?> c when c == BinToDec.class -> "converts the selected binary to a decimal number";
            case Class<?> c when c == Backward.class -> "history back on tab";
            case Class<?> c when c == Forward.class -> "history forward on tab";
            case Class<?> c when c == ZoomIn.class -> "enlarge the font size";
            case Class<?> c when c == ZoomOut.class -> "reduce the font size";
            case Class<?> c when c == FindAll.class -> "searches for the specified text";
            case Class<?> c when c == GoTo.class -> "go to the specified number of row";
            case Class<?> c when c == Filter.class -> "not implemented yet";
            case Class<?> c when c == WrapLine.class -> "wraps a line with a specified number of characters";
            case Class<?> c when c == Open.class -> "opens the file at the specified path";
            case Class<?> c when c == Help.class -> "show help dialog";
            case null, default -> "";
        };
    }

    static String shortcutText(Class<? extends Command> clazz) {
        var shortcut = System.getProperty("os.name").toLowerCase().contains("mac") ? "⌘" : "Ctrl";
        return switch (clazz) {
            case Class<?> c when c == OpenChoose.class -> shortcut + " O";
            case Class<?> c when c == Save.class -> shortcut + " S";
            case Class<?> c when c == SaveAs.class -> shortcut + " Shift S";
            case Class<?> c when c == New.class -> shortcut + " N";
            case Class<?> c when c == TabClose.class -> shortcut + " W";
            case Class<?> c when c == FindAll.class -> shortcut + " F";
            case Class<?> c when c == Config.class -> shortcut + " ,";
            case Class<?> c when c == ZoomIn.class -> shortcut + " +";
            case Class<?> c when c == ZoomOut.class -> shortcut + " -";
            case Class<?> c when c == Forward.class -> shortcut + " ]";
            case Class<?> c when c == Backward.class -> shortcut + " [";
            case Class<?> c when c == Help.class -> "F1";
            case null, default -> "";
        };
    }

    static Map<String, Class<? extends Command>> values() {
        @SuppressWarnings("unchecked")
        var permitted = (Class<? extends Command>[]) Command.class.getPermittedSubclasses();
        return Arrays.stream(permitted)
            .filter(not(Hidden.class::isAssignableFrom))
            .collect(Collectors.toMap(
                Class::getSimpleName,
                UnaryOperator.identity(),
                (_, e2) -> e2,
                LinkedHashMap::new));
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
                Object argObj = String.class.isAssignableFrom(argType)
                    ? args[0]
                    : argType.getMethod("valueOf", String.class).invoke(null, args[0]);
                Constructor<? extends Command> constructor = clazz.getConstructor(argType);
                return constructor.newInstance(argType.cast(argObj));
            } catch (Exception ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }
        return new Empty();
    }

}
