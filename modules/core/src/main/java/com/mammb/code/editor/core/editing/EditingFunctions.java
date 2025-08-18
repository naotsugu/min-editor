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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The editing functions.
 * @author Naotsugu Kobayashi
 */
public interface EditingFunctions {

    /** logger. */
    System.Logger log = System.getLogger(EditingFunctions.class.getName());

    /** HTML escape or numeric character reference. */
    Pattern htmlUnescapePattern = Pattern.compile("&(?:#(?<type>x?)(?<digit>[0-9a-fA-F]+)|(?<name>[a-zA-Z]+));");

    /** Pass through function. */
    Function<String, String> passThrough = text -> text;

    /**
     * Sanitize function.
     * allow HT, LF, CR, SP
     * U+2028: LINE_SEPARATOR(Zl category)
     * U+2029: PARAGRAPH_SEPARATOR(Zp category)
     */
    Function<String, String> sanitize = text -> text == null ? "" : text.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f\\x7f\\u2028\\u2029]", "");

    /** To lower case function. */
    Function<String, String> toLower = text -> text == null ? "" : text.toLowerCase();
    /** To upper case function. */
    Function<String, String> toUpper = text -> text == null ? "" : text.toUpperCase();

    /** To indent paren. */
    Function<String, String> toIndentParen = text -> StackIndents.indentify(text, '(', ')').toString();
    /** To indent curly brace. */
    Function<String, String> toIndentCurlyBrace = text -> StackIndents.indentify(text, '{', '}').toString();

    /** Indent function. */
    Function<String, String> indent = text -> text == null ? "" : Arrays.stream(text.split("(?<=\\n)"))
        .map(s -> " ".repeat(4) + s)
        .collect(Collectors.joining());
    /** Un indent functions. */
    Function<String, String> unindent = text -> text == null ? "" : Arrays.stream(text.split("(?<=\\n)"))
        .map(s -> s.replaceFirst("^ {1,4}|^\t", ""))
        .collect(Collectors.joining());

    /** Sort function. */
    Function<String, String> sort = text -> text == null ? "" : Arrays.stream(text.split("(?<=\\n)"))
        .sorted().collect(Collectors.joining());

    /** Unique function. */
    Function<String, String> unique = text -> text == null ? "" : Arrays.stream(text.split("(?<=\\n)"))
        .distinct().collect(Collectors.joining());

    /** Calc function. */
    Function<String, String> toCalc = EditingFunctions::calc;

    /** decToHex function. */
    Function<String, String> decToHex = text -> hex(text, 10);
    /** decToBin function. */
    Function<String, String> decToBin = text -> bin(text, 10);
    /** hexToBin function. */
    Function<String, String> hexToBin = text -> bin(text.toLowerCase().startsWith("0x") ? text.substring(2) : text, 16);
    /** hexToDec function. */
    Function<String, String> hexToDec = text -> dec(text.toLowerCase().startsWith("0x") ? text.substring(2) : text, 16);
    /** binToHex function. */
    Function<String, String> binToHex = text -> hex(text.replaceAll(" ", ""), 2);
    /** binToDec function. */
    Function<String, String> binToDec = text -> dec(text.replaceAll(" ", ""), 2);

    /** binToDec function. */
    Function<String, String> lfToCrLf = text -> text == null ? "" : text.replaceAll("(?<!\r)\n", "\r\n");
    /** binToDec function. */
    Function<String, String> crLfToLf = text -> text == null ? "" : text.replaceAll("\r\n", "\n");

    /** markdown table. */
    Function<String, String> markdownTable = MarkdownTables::fromHtml;

    Function<String, String> htmlBrToLf = text -> text == null ? "" : text.replaceAll("(?i)<br\\b[^>]*>", "\n");

    /** remove tag like. */
    Function<String, String> removeHtmlTags = text -> text == null ? "" : text.replaceAll("<.*?>", "");

    Function<String, String> unescapeHtml = text -> text == null ? "" : htmlUnescapePattern.matcher(text).replaceAll(match -> {
        if (match.group("name") != null) {
            String name = match.group("name");
            return Map.of("lt", "<", "gt", ">", "amp", "&", "quot", "\"", "apos", "'", "nbsp", " ")
                .getOrDefault(name, match.group(0));
        }
        try {
            return String.valueOf(
                (char) Integer.parseInt(match.group("digit"),
                "x".equalsIgnoreCase(match.group("type")) ? 16 : 10));
        } catch (NumberFormatException e) {
            return match.group(0);
        }
    });

    /** html to markdown. */
    Function<String, String> htmlToMarkdown = text -> text == null ? "" : markdownTable
        .andThen(htmlBrToLf).andThen(removeHtmlTags).andThen(unescapeHtml).andThen(unescapeHtml)
        .apply(htmlAnchorToMarkdownLink(text));

    /** ls. */
    Function<List<Path>, String> list = EditingFunctions::list;

    // -- helper --------------------------------------------------------------

    private static String calc(String text) {

        if (text == null || text.isEmpty()) return "";

        var sb = new StringBuilder();
        for (String line : text.split("(?<=\\R)")) {

            String[] split = line.splitWithDelimiters("\\R", 2);
            String tail = split.length > 1 ? split[1] : "";

            // if it contains an equal sign, delete the rest
            int eq = split[0].indexOf('=');
            String formula = (eq > 1) ? split[0].substring(0, eq) : split[0];
            try {
                String s = formula.contains(" ") ? " " : "";
                String r = "%s%s=%s%s".formatted(formula, s, s, Calculator.calc(formula));
                sb.append(r).append(tail);
            } catch (Exception ignore) {
                log.log(System.Logger.Level.WARNING, ignore);
                sb.append(line);
            }
        }
        return sb.toString();
    }

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

    private static String list(List<Path> paths) {
        var sb = new StringBuilder();
        for (Path path : paths) {
            if (!Files.exists(path)) continue;
            if (Files.isDirectory(path) && Files.isReadable(path)) {
                try (var stream = Files.list(path)) {
                    String list = stream.map(Path::toAbsolutePath)
                        .map(Path::toString).collect(Collectors.joining("\n"));
                    sb.append(list);
                } catch (IOException ignore) {  }
            } else {
                sb.append(path.toAbsolutePath());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private static String htmlAnchorToMarkdownLink(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        String regex = "<a\\s+href=\"([^\"]+)\"[^>]*>([^<]+)</a>";
        String replacement = "[$2]($1)";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        return matcher.replaceAll(replacement);
    }

}
