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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;

/**
 * The MarkdownTables.
 * @author Naotsugu Kobayashi
 */
class MarkdownTables {

    private static final Pattern tablePattern   = Pattern.compile("<table.*?>(.*?)</table>", CASE_INSENSITIVE | DOTALL);
    private static final Pattern captionPattern = Pattern.compile("<caption.*?>(.*?)</caption>", CASE_INSENSITIVE | DOTALL);
    private static final Pattern recordPattern  = Pattern.compile("<tr.*?>(.*?)</tr>", CASE_INSENSITIVE | DOTALL);
    private static final Pattern colPattern     = Pattern.compile("<t[hd ].*?>(.*?)</t[hd]>", CASE_INSENSITIVE | DOTALL);

    /**
     * Convert HTML tables to markdown tables.
     * @param html source html text
     * @return markdown text
     */
    static String fromHtml(String html) {

        var sb = new StringBuilder(html.length());
        int index = 0;

        Matcher m = tablePattern.matcher(html);
        while (m.find()) {

            if (m.start() > index) {
                sb.append(html, index, m.start());
                sb.append('\n');
            }
            index = m.end();

            var table = m.group(1);
            if (table.isEmpty()) continue;
            sb.append(handleCaption(table));
            sb.append('\n');
            sb.append(handleTable(m.group(1)));
        }
        sb.append(html.substring(index));

        return sb.toString();
    }

    // -- private helper ------------------------------------------------------

    private static CharSequence handleCaption(String table) {
        var sb = new StringBuilder();
        Matcher m = captionPattern.matcher(table);
        while (m.find()) {
            sb.append(m.group(1).trim());
            sb.append('\n');
        }
        return sb;
    }

    private static CharSequence handleTable(String table) {
        var sb = new StringBuilder();
        Matcher m = recordPattern.matcher(table);
        while (m.find()) {
            var row = handleRow(m.group(1));
            if (row.isEmpty()) continue;
            boolean header = sb.isEmpty();
            sb.append(row);
            sb.append('\n');
            if (header) {
                sb.append(row.toString().replaceAll("[^\\|]+", " --- "));
                sb.append('\n');
            }
        }
        return sb;
    }

    private static CharSequence handleRow(String row) {
        var sb = new StringBuilder();
        Matcher m = colPattern.matcher(row);
        while (m.find()) {
            sb.append("| ");
            sb.append(m.group(1).trim());
            sb.append(" ");
        }
        if (!sb.isEmpty()) {
            sb.append("|");
        }
        return sb;
    }

}
