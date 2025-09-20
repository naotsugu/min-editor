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
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.layout.ScreenLayout;
import com.mammb.code.editor.core.tools.BinaryView;
import com.mammb.code.editor.core.tools.Source16;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

/**
 * The session utilities for the model.
 * @author Naotsugu Kobayashi
 */
public class Sessions {

    /** logger. */
    private static final System.Logger log = System.getLogger(Sessions.class.getName());

    static abstract class PointAtTransformer implements Session.Transformer {
        protected final ScreenLayout screenLayout;
        protected final Point point;
        public PointAtTransformer(ScreenLayout screenLayout, Point point) {
            this.screenLayout = screenLayout;
            this.point = point;
        }
    }

    public static class Stash extends PointAtTransformer {
        public Stash(ScreenLayout screenLayout, Point point) {
            super(screenLayout, point);
        }
        @Override
        public Session apply(Context ctx, Content content) {
            if (content.query(Query.size) <= 0) {
                return Session.empty();
            }

            Path stashPath = writeStash(ctx, content);
            if (stashPath == null) {
                log.log(System.Logger.Level.ERROR, "failed to write stash file");
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
                point.row(), point.col());
        }
    }

    public static class Current extends PointAtTransformer {
        public Current(ScreenLayout screenLayout, Point point) {
            super(screenLayout, point);
        }
        @Override
        public Session apply(Context ctx, Content content) {
            var path = content.path().orElse(null);
            return Session.of(
                path,
                Files.lastModifiedTime(path),
                null,
                "",
                content.query(Query.charCode),
                content.readonly(),
                screenLayout.topLine(), screenLayout.charsInLine(),
                point.row(), point.col());
        }
    }


    public static class Diff implements Session.Transformer {
        private final Path path;
        private final boolean withoutFold;
        public Diff(Path path, boolean withoutFold) {
            this.path = path;
            this.withoutFold = withoutFold;
        }
        @Override
        public Session apply(Context ctx, Content content) {
            String name = content.query(Query.modelName).plain() + ".diff";
            Path stashPath = ctx.config().stashPath().resolve(String.join(
                "_", UUID.randomUUID().toString(), name));
            DiffRun diffRun = (path == null) ? DiffRun.of(content) : DiffRun.of(content, path);
            return Session.of(
                null,
                null,
                withoutFold ? diffRun.writeWithoutFold(stashPath) : diffRun.write(stashPath),
                name,
                StandardCharsets.UTF_8,
                false,
                0, 0, 0, 0);
        }
    }

    public static class Binary implements Session.Transformer {
        private final Path path;
        public Binary(Path path) {
            this.path = path;
        }
        @Override
        public Session apply(Context ctx, Content content) {
            if (path == null) {
                return Session.empty();
            }

            String name = content.query(Query.modelName).plain() + ".binary";
            Path outPath = ctx.config().stashPath().resolve(String.join(
                "_", UUID.randomUUID().toString(), name));
            Path view = Files.write(outPath, BinaryView.run(new Source16(path)));

            return Session.of(
                null,
                null,
                view,
                name,
                StandardCharsets.UTF_8,
                false,
                0, 0, 0, 0);
        }
    }

    private static Path writeStash(Context ctx, Content content) {

        Path stashPath = ctx.config().stashPath().resolve(
            String.join("_", UUID.randomUUID().toString(),
                content.query(Query.modelName).plain()));

        try {
            content.write(stashPath);
            return stashPath;
        } catch (Exception ignore) {
            log.log(System.Logger.Level.ERROR, "failed to write stash file: " + stashPath, ignore);
        }
        return null;
    }

}
