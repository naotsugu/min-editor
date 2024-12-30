/*
 * Copyright 2022-2024 the original author or authors.
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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Action.*;
import com.mammb.code.editor.core.Clipboard;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * The ActionRecords.
 * @author Naotsugu Kobayashi
 */
public interface ActionRecords {

    record Empty(long occurredAt) implements Action {
        @Override public boolean isEmpty() { return true; }
    }
    record Input(String attr, long occurredAt) implements Action, WithAttr<String>, Repeatable { }
    record Replace(Function<String, String> attr, boolean keepSelect, long occurredAt) implements Action, WithAttr<Function<String, String>>, Repeatable { }
    record Delete(long occurredAt) implements Action, Repeatable { }
    record Backspace(long occurredAt) implements Action, Repeatable { }
    record Undo(long occurredAt) implements Action, Repeatable { }
    record Redo(long occurredAt) implements Action, Repeatable { }
    record SelectAll(long occurredAt) implements Action, Repeatable { }
    record Escape(long occurredAt) implements Action, Repeatable { }
    record Repeat(long occurredAt) implements Action { }

    record Save(Path attr, long occurredAt) implements Action, WithAttr<Path> { }
    record WrapLine(Integer attr, long occurredAt) implements Action, WithAttr<Integer> { }
    record Goto(Integer attr, long occurredAt) implements Action, WithAttr<Integer> { }
    record FindAll(String attr, long occurredAt) implements Action, WithAttr<String> { }

    record Copy(Clipboard attr, long occurredAt) implements Action, WithAttr<Clipboard>, Repeatable { }
    record Cut(Clipboard attr, long occurredAt) implements Action, WithAttr<Clipboard>, Repeatable { }
    record Paste(Clipboard attr, long occurredAt) implements Action, WithAttr<Clipboard>, Repeatable { }

    record Home(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record End(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record Tab(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record CaretRight(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record CaretLeft(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record CaretUp(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record CaretDown(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record PageUp(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }
    record PageDown(boolean withSelect, long occurredAt) implements Action, WithSelect, Repeatable { }

}
