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

/**
 * The ui model module.
 * @author Naotsugu Kobayashi
 */
module code.editor.ui.model {

    requires code.editor.javafx;
    requires code.editor.model.buffer;
    requires code.editor.javafx.layout;
    requires code.editor.model.text;
    requires code.editor.model.layout;
    requires code.editor.model.edit;
    requires code.editor.model.find;
    requires code.editor.syntax;
    requires code.editor.ui.prefs;

    exports com.mammb.code.editor.ui.model;
    exports com.mammb.code.editor.ui.model.editing;

}
