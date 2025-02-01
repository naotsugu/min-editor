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
package com.mammb.code.editor.core.syntax2;

import java.util.List;
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
     * @param row the number of row
     * @param text the row text
     * @return the list of StyleSpan
     */
    List<Style.StyleSpan> apply(int row, String text);

    /**
     * Get the {@link BlockScopes).
     * @return the {@link BlockScopes)
     */
    default BlockScopes blockScopes() { return null; };

    /**
     * Gets whether the block scope has.
     * @return {@code true}, if the block scope has
     */
    default boolean hasBlockScopes() {
        var blockScopes = blockScopes();
        return blockScopes != null && !blockScopes.isEmpty();
    }

    /**
     * Get the syntax for a given name.
     * @param name the name
     * @return the syntax
     */
    static Syntax of(String name) {

        if (name == null) {
            name = "";
        }

        record PassThrough(String name) implements Syntax {
            @Override public List<Style.StyleSpan> apply(int row, String text) {
                return List.of();
            }
        }

        return switch (name.toLowerCase()) {
            case "java" -> new JavaSyntax();
            case "md" -> new MarkdownSyntax();
            case "kotlin", "kt", "kts" -> new KotlinSyntax();
            case "js", "json" -> new JsSyntax();
            case "rs" -> new RustSyntax();
            case "sql" -> new SqlSyntax();
            case "py" -> new PythonSyntax();
            case "cpp", "c" -> new CppSyntax();
            case "go" -> new GoSyntax();
            case "ts", "tsx" -> new TsSyntax();
            case "html", "htm", "xhtml" -> new HtmlSyntax();
            case "yaml", "yml" -> new YamlSyntax();
            default -> new PassThrough(name);
        };
    }

}
