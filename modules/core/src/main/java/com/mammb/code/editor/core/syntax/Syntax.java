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
package com.mammb.code.editor.core.syntax;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.syntax.lang.CppSyntax;
import com.mammb.code.editor.core.syntax.lang.DiffSyntax;
import com.mammb.code.editor.core.syntax.lang.GoSyntax;
import com.mammb.code.editor.core.syntax.lang.HtmlSyntax;
import com.mammb.code.editor.core.syntax.lang.IniSyntax;
import com.mammb.code.editor.core.syntax.lang.JavaSyntax;
import com.mammb.code.editor.core.syntax.lang.JsSyntax;
import com.mammb.code.editor.core.syntax.lang.KotlinSyntax;
import com.mammb.code.editor.core.syntax.lang.MarkdownSyntax;
import com.mammb.code.editor.core.syntax.lang.PowerShellSyntax;
import com.mammb.code.editor.core.syntax.lang.PythonSyntax;
import com.mammb.code.editor.core.syntax.lang.RustSyntax;
import com.mammb.code.editor.core.syntax.lang.ShellSyntax;
import com.mammb.code.editor.core.syntax.lang.SqlSyntax;
import com.mammb.code.editor.core.syntax.lang.TomlSyntax;
import com.mammb.code.editor.core.syntax.lang.TsSyntax;
import com.mammb.code.editor.core.syntax.lang.CsvSyntax;
import com.mammb.code.editor.core.syntax.lang.TsvSyntax;
import com.mammb.code.editor.core.syntax.lang.YamlSyntax;
import com.mammb.code.editor.core.text.Style;

/**
 * The syntax.
 * @author Naotsugu Kobayashi
 */
public interface Syntax {

    /**
     * Get the name
     * @return the name
     */
    String name();

    /**
     * Apply syntax highlights
     * @param row the number of rows
     * @param text the row text
     * @return the list of StyleSpan
     */
    List<Style.StyleSpan> apply(int row, String text);

    /**
     * Get the {@link BlockScopes).
     * @return the {@link BlockScopes)
     */
    default BlockScopes blockScopes() { return null; }

    /**
     * Gets whether the block scope has.
     * @return {@code true}, if the block scope has
     */
    default boolean hasBlockScopes() {
        var blockScopes = blockScopes();
        return blockScopes != null && !blockScopes.isEmpty();
    }

    /**
     * Get the default syntax.
     * The default syntax is markdown.
     * @return the syntax
     */
    static Syntax of() {
        return of("markdown");
    }

    /**
     * Get the syntax for a given path.
     * @param path the path
     * @return the syntax
     */
    static Syntax pathOf(Path path) {
        return of(syntaxName(path));
    }

    /**
     * Get the syntax for a given path string.
     * @param path the path string
     * @return the syntax
     */
    static Syntax pathOf(String path) {
        if (path == null || path.indexOf('.') < 0) {
            return of();
        }
        var extension = path.substring(path.lastIndexOf(".") + 1);
        return of(syntaxName(extension));
    }

    /**
     * Get the syntax for a given name.
     * @param name the name
     * @return the syntax
     */
    static Syntax of(String name) {

        // the pass-through syntax
        record PassThrough(String name) implements Syntax {
            @Override
            public List<Style.StyleSpan> apply(int row, String text) {
                return List.of();
            }
        }

        return switch (name) {
            case "java" -> new JavaSyntax();
            case "markdown" -> new MarkdownSyntax();
            case "kotlin" -> new KotlinSyntax();
            case "js" -> new JsSyntax();
            case "rust" -> new RustSyntax();
            case "sql" -> new SqlSyntax();
            case "python" -> new PythonSyntax();
            case "cpp" -> new CppSyntax();
            case "go" -> new GoSyntax();
            case "ts" -> new TsSyntax();
            case "html" -> new HtmlSyntax();
            case "yaml" -> new YamlSyntax();
            case "toml" -> new TomlSyntax();
            case "ini" -> new IniSyntax();
            case "csv" -> new CsvSyntax();
            case "tsv" -> new TsvSyntax();
            case "diff" -> new DiffSyntax();
            case "text" -> new PassThrough("text");
            case "shell" -> new ShellSyntax();
            case "ps1" -> new PowerShellSyntax();
            case null, default -> new PassThrough(name);
        };
    }

    /**
     * Get the syntax name by the specified path.
     * @param path the specified path
     * @return the syntax name
     */
    static String syntaxName(Path path) {
        return syntaxName(extension(path));
    }

    /**
     * Get the extension string.
     * @param path the path
     * @return the extension string
     */
    private static String extension(Path path) {
        return Optional.ofNullable(path)
            .map(Path::getFileName)
            .map(Path::toString)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(f.lastIndexOf(".") + 1))
            .orElse("");
    }

    /**
     * Get the syntax name from the extension.
     * @param extension the extension
     * @return the syntax name
     */
    private static String syntaxName(String extension) {
        extension = (extension == null) ? "" : extension.toLowerCase();
        return switch (extension) {
            case "md" -> "markdown";
            case "kotlin", "kt", "kts" -> "kotlin";
            case "js", "json" -> "js";
            case "rs" -> "rust";
            case "py" -> "python";
            case "cpp", "c" -> "cpp";
            case "ts", "tsx" -> "ts";
            case "html", "htm", "xhtml" -> "html";
            case "yaml", "yml" -> "yaml";
            case "txt" -> "text";
            case "shell", "sh", "bash" -> "shell";
            default -> extension;
        };
    }

}
