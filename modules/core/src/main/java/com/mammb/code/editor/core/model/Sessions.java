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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Caret;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Context;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.layout.ScreenLayout;
import java.nio.file.Path;
import java.util.UUID;

/**
 * The session utilities for the model.
 * @author Naotsugu Kobayashi
 */
class Sessions {

    /** logger. */
    private static final System.Logger log = System.getLogger(Sessions.class.getName());


    static Session stash(Context ctx, Content content, ScreenLayout screenLayout, Caret caret) {

        if (content.query(Query.size) <= 0) {
            return Session.empty();
        }

        Path stashPath = ctx.config().stashPath().resolve(
            String.join("_", UUID.randomUUID().toString(),
            content.query(Query.modelName).plain()));

        try {
            content.write(stashPath);
        } catch (Exception ignore) {
            log.log(System.Logger.Level.ERROR, "failed to write stash file: " + stashPath, ignore);
            return Session.empty();
        }

        return Session.of(
            content.path().orElse(null),
            content.path().map(Files::lastModifiedTime).orElse(null),
            stashPath,
            content.query(Query.modelName).plain(),
            content.query(Query.charCode),
            content.readonly(),
            screenLayout.topLine(), screenLayout.charsInLine(),
            caret.row(), caret.col());
    }

    static Session current(Content content, ScreenLayout screenLayout, Caret caret) {
        var path = content.path().orElse(null);
        return Session.of(
            path,
            Files.lastModifiedTime(path),
            null,
            "",
            content.query(Query.charCode),
            content.readonly(),
            screenLayout.topLine(), screenLayout.charsInLine(),
            caret.row(), caret.col());
    }

}
