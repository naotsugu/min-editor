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
package com.mammb.code.editor.core.syntax;

import com.mammb.code.editor.core.Clipboard;
import java.util.function.Consumer;

/**
 * Represents a handler for managing paste operations.
 * Provides mechanisms for processing or customizing the behavior
 * of pasting content in a specific context.
 * @author Naotsugu Kobayashi
 */
public interface PasteHandler {

    /**
     * Handles the clipboard's paste operation by processing or customizing its behavior.
     * @param clipboard the clipboard instance that contains content to be processed
     * @param pasteConsumer a consumer to handle the processed content after customization
     * @return {@code true} if the operation was successfully handled, or {@code false} otherwise
     */
    boolean handle(Clipboard clipboard, Consumer<String> pasteConsumer);

}
