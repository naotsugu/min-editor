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
package com.mammb.code.editor.model.find.impl;

import com.mammb.code.editor.model.content.Content;
import com.mammb.code.editor.model.find.Find;
import com.mammb.code.editor.model.find.FindSpec;
import com.mammb.code.editor.model.find.Match;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.until.Traverse;
import com.mammb.code.editor.model.until.Until;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Find.
 * @author Naotsugu Kobayashi
 */
public class FindImpl implements Find {

    /** The content. */
    private Content content;

    /** The listeners of find. */
    private List<Consumer<Textual>> listeners;

    private volatile boolean interrupt = false;


    public FindImpl(Content content, List<Consumer<Textual>> listeners) {
        this.content = content;
        this.listeners = listeners;
    }

    public FindImpl(Content content) {
        this(content, new ArrayList<>());
    }

    public void run(OffsetPoint base, FindSpec spec) {

        Traverse traverse = Traverse.forwardOf(base);
        Until<byte[]> until = Until.lfInclusive().with(traverse);

        for (;;) {
            if (interrupt) {
                interrupt = false;
                return;
            }
            byte[] bytes = content.bytes(traverse.asOffsetPoint().cpOffset(), until);
            if (bytes.length == 0) {
                break;
            }
            String row = new String(bytes, StandardCharsets.UTF_8);
            var result = spec.match(row);
            for (Match match : result) {
                OffsetPoint point = base.plus(row.substring(0, match.end()));
                String sub = row.substring(match.start(), match.end());
                listeners.forEach(listener -> listener.accept(Textual.of(point, sub)));
                if (spec.oneshot()) {
                    return;
                }
            }
        }
    }

    @Override
    public void cancel() {
        this.interrupt = true;
    }

    @Override
    public void addListener(Consumer<Textual> listener) {
        listeners.add(listener);
    }

}
