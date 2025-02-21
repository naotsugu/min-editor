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

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import com.mammb.code.editor.core.Action;

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

    interface RequireArgs2<T1, T2> extends RequireArgs { }

    record ActionCommand(Action action) implements Command, Hidden {}

    record OpenChoose() implements Command {}

    record Save() implements Command {}

    record SaveAs() implements Command {}

    record New() implements Command {}

    record TabClose() implements Command {}

    record Config() implements Command {}

    record Palette(Class<? extends Command> initial) implements Command, Hidden {}

    record ToLowerCase() implements Command {}

    record ToUpperCase() implements Command {}

    record IndentParen() implements Command {}

    record IndentCurlyBrace() implements Command {}

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

    record FindNext(String str, Boolean caseSensitive) implements Command, RequireArgs2<String, Boolean> { }

    record FindPrev(String str, Boolean caseSensitive) implements Command, RequireArgs2<String, Boolean> { }

    record FindAll(String str, Boolean caseSensitive) implements Command, RequireArgs2<String, Boolean> { }

    record FindNextRegex(String str) implements Command, RequireArgs1<String> { }

    record FindPrevRegex(String str) implements Command, RequireArgs1<String> { }

    record FindAllRegex(String str) implements Command, RequireArgs1<String> { }

    record SelectAllRegex(String str) implements Command, RequireArgs1<String> { }

    record GoTo(Integer rowNumber) implements Command, RequireArgs1<Integer> { }

    record Filter(String str) implements Command, RequireArgs1<String> { }

    record WrapLine(Integer width) implements Command, RequireArgs1<Integer> { }

    record ToggleLayout() implements Command { }

    record Open(String path) implements Command, RequireArgs1<String> { }

    record Help() implements Command {}

    record Empty() implements Command, Hidden {}

    static String promptText(Class<? extends Command> clazz) {
        return switch (clazz) {
            case Class<?> c when c == FindNext.class -> "[string to search] [`true` if case insensitive]";
            case Class<?> c when c == FindPrev.class -> "[string to search] [`true` if case insensitive]";
            case Class<?> c when c == FindNextRegex.class -> "[regex to search]";
            case Class<?> c when c == FindPrevRegex.class -> "[regex to search]";
            case Class<?> c when c == FindAll.class -> "[string to search] [`true` if case insensitive]";
            case Class<?> c when c == FindAllRegex.class -> "[regex to search]";
            case Class<?> c when c == SelectAllRegex.class -> "[regex to select]";
            case Class<?> c when c == GoTo.class -> "[line number]";
            case Class<?> c when c == Filter.class -> "[regexp string to filter]";
            case Class<?> c when c == WrapLine.class -> "[wrap width(number of characters)]";
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
            case Class<?> c when c == IndentParen.class -> "indent by paren";
            case Class<?> c when c == IndentCurlyBrace.class -> "indent by curly brace";
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
            case Class<?> c when c == FindNext.class -> "searches for the next occurrence";
            case Class<?> c when c == FindPrev.class -> "searches for the previous occurrence";
            case Class<?> c when c == FindAll.class -> "searches for the specified text";
            case Class<?> c when c == FindNextRegex.class -> "searches for the next occurrence";
            case Class<?> c when c == FindPrevRegex.class -> "searches for the previous occurrence";
            case Class<?> c when c == FindAllRegex.class -> "searches for the specified regex";
            case Class<?> c when c == SelectAllRegex.class -> "selects for the specified regex";
            case Class<?> c when c == GoTo.class -> "go to the specified number of row";
            case Class<?> c when c == Filter.class -> "not implemented yet";
            case Class<?> c when c == WrapLine.class -> "wraps a line with a specified number of characters";
            case Class<?> c when c == ToggleLayout.class -> "toggle context layout";
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
            case Class<?> c when c == FindNext.class -> "F3";
            case Class<?> c when c == FindPrev.class -> shortcut + " F3";
            case Class<?> c when c == FindNextRegex.class -> "F3";
            case Class<?> c when c == FindPrevRegex.class -> shortcut + " F3";
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
        try {
            if (!RequireArgs.class.isAssignableFrom(clazz)) {
                return clazz.getDeclaredConstructor().newInstance();

            } else if (RequireArgs1.class.isAssignableFrom(clazz)) {
                Class<?> argType = argType(clazz, 0);
                return clazz.getConstructor(argType).newInstance(argType.cast(argObj(argType, args, 0)));

            } else if (RequireArgs2.class.isAssignableFrom(clazz)) {
                Class<?>[] argTypes = new Class<?>[] { argType(clazz, 0), argType(clazz, 1) };
                return clazz.getConstructor(argTypes).newInstance(
                    argTypes[0].cast(argObj(argTypes[0], args, 0)),
                    argTypes[1].cast(argObj(argTypes[1], args, 1)));
            }

        } catch (Exception ignore) {
            log.log(System.Logger.Level.ERROR, ignore);
        }
        return new Empty();
    }

    private static Class<?> argType(Class<? extends Command> clazz, int index) {
        return Arrays.stream(clazz.getGenericInterfaces())
            .filter(ParameterizedType.class::isInstance)
            .map(ParameterizedType.class::cast)
            .map(type -> type.getActualTypeArguments()[index])
            .filter(Class.class::isInstance)
            .map(Class.class::cast)
            .findFirst().get();
    }

    private static Object argObj(Class<?> argType, String[] args, int index) {
        return switch (argType) {
            case Class<?> c when c == String.class -> toString(args, index);
            case Class<?> c when c == Boolean.class -> toBoolean(args, index);
            case Class<?> c when c == Integer.class -> toInt(args, index);
            case Class<?> c when c == Character.class -> toChar(args, index);
            case null, default -> null;
        };
    }

    private static String toString(String[] args, int index) {
        if (args.length - 1 < index || args[index] == null) {
            return "";
        }
        return args[index];
    }

    private static char toChar(String[] args, int index) {
        if (args.length - 1 < index || args[index] == null) {
            return 0;
        }
        return args[index].charAt(0);
    }

    private static int toInt(String[] args, int index) {
        if (args.length - 1 < index || args[index] == null) {
            return 0;
        }
        try {
            return Integer.parseInt(args[index]);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private static boolean toBoolean(String[] args, int index) {
        if (args.length - 1 < index || args[index] == null) {
            return false;
        }
        String arg = args[index].toLowerCase();
        return Objects.equals(arg, "t") ||
            Objects.equals(arg, "true") ||
            Objects.equals(arg, "on") ||
            Objects.equals(arg, "yes") ||
            Objects.equals(arg, "y") ||
            Objects.equals(arg, "1");
    }

}
