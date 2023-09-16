/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.syntax;

import com.mammb.code.editor.model.style.StylingTranslate;
import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerProvider;
import com.mammb.code.editor.syntax.base.PassThroughLexer;
import com.mammb.code.editor.syntax.base.SyntaxTranslate;
import com.mammb.code.editor.syntax.basic.DiffLexer;
import com.mammb.code.editor.syntax.java.JavaLexer;
import com.mammb.code.editor.syntax.javascript.JsonLexer;
import com.mammb.code.editor.syntax.markdown.MarkdownLexer;
import com.mammb.code.editor.syntax.rust.RustLexer;

import java.nio.file.Path;

/**
 * Syntax.
 * @author Naotsugu Kobayashi
 */
public class Syntax {

    /** The lexer provider. */
    private static final LexerProvider lexerProvider = new LexerProvider() {
        @Override
        public Lexer get(String name) {
            return switch (name) {
                case "java"    -> new JavaLexer();
                case "rust"    -> new RustLexer();
                case "json"    -> new JsonLexer();
                case "md"      -> new MarkdownLexer(lexerProvider);
                case "diff"    -> new DiffLexer();
                default        -> new PassThroughLexer(name);
            };
        }
    };

    /**
     * Get the StylingTranslate for the specified path file type.
     * @param path the path of the target file
     * @param baseColor
     * @return the StylingTranslate
     */
    public static StylingTranslate of(Path path, String baseColor) {
        Lexer lexer = lexerProvider.get(getExtension(path).toLowerCase());
        return (lexer instanceof PassThroughLexer)
            ? StylingTranslate.passThrough()
            : new SyntaxTranslate(lexer, baseColor);
    }


    /**
     * Get the extension name.
     * @param path the source path
     * @return the extension name
     */
    private static String getExtension(Path path) {
        if (path == null) {
            return "";
        }
        String fileName = path.getFileName().toString();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

}
