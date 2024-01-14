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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.find.Find;
import com.mammb.code.editor.model.find.FindSpec;
import com.mammb.code.editor.model.find.Found;
import com.mammb.code.editor.model.find.FoundNone;
import com.mammb.code.editor.model.find.FoundRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.FindHandle;
import java.util.function.Consumer;

/**
 * FindHandle.
 * @author Naotsugu Kobayashi
 */
public class FindHandleImpl implements FindHandle {

    /** The find. */
    private final Find find;

    /** The base point. */
    private final OffsetPoint basePoint;

    /** The found first action. */
    private final Consumer<Found> foundFirstAction;


    /**
     * Constructor.
     * @param find the find
     * @param basePoint the base point
     * @param foundFirstAction the found first action
     */
    private FindHandleImpl(Find find, OffsetPoint basePoint, Consumer<Found> foundFirstAction) {
        this.find = find;
        this.basePoint = basePoint;
        this.foundFirstAction = foundFirstAction;
    }


    /**
     * Create the FindHandle.
     * @param find the find
     * @param basePoint the base point
     * @param foundFirstAction the found first action
     * @return the FindHandle
     */
    public static FindHandle of(Find find, OffsetPoint basePoint, Consumer<Found> foundFirstAction) {
        return new FindHandleImpl(find, basePoint, foundFirstAction);
    }


    @Override
    public void findNext(String string, boolean regexp, boolean forward) {
        Found found = findFirst(FindSpec.of(string, forward));
        foundFirstAction.accept(found);
    }


    @Override
    public void findAll(String string, boolean regexp) {
        Found found = findFirst(FindSpec.of(string, true));
        foundFirstAction.accept(found);
        if (found instanceof FoundRun) {
            find.run(OffsetPoint.zero, FindSpec.allOf(string));
        }
    }


    private Found findFirst(FindSpec findSpec) {
        final var consumer = new FoundFirstConsumer();
        try {
            find.addListener(consumer);
            find.run(basePoint, findSpec);
        } finally {
            find.removeListener(consumer);
        }
        return consumer.foundFirst;
    }


    private static class FoundFirstConsumer implements Consumer<Found> {
        Found foundFirst = new FoundNone();
        @Override
        public void accept(Found found) {
            if (foundFirst instanceof FoundNone) foundFirst = found;
        }
    }

}
