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
    private OffsetPoint basePoint;

    /** The found first action. */
    private Consumer<Found> foundFirstAction = found -> { };


    public FindHandleImpl(Find find, OffsetPoint basePoint) {
        this.find = find;
        this.basePoint = basePoint;
    }


    @Override
    public void findNext(String string, boolean regexp) {
        runWithFirstAction(() -> find.run(basePoint, FindSpec.of(string)));
    }


    @Override
    public void findAll(String string, boolean regexp) {
        runWithFirstAction(() -> find.run(basePoint, FindSpec.allOf(string)));
    }


    private void runWithFirstAction(Runnable runnable) {
        final var flashConsumer = new FlashConsumer(foundFirstAction);
        try {
            find.addListener(flashConsumer);
            runnable.run();
        } finally {
            find.removeListener(flashConsumer);
        }
    }


    static class FlashConsumer implements Consumer<Found> {

        private final Consumer<Found> actualAction;
        private boolean already = false;

        public FlashConsumer(Consumer<Found> actualAction) {
            this.actualAction = actualAction;
        }

        @Override
        public void accept(Found found) {
            if (!already) {
                actualAction.accept(found);
                already = true;
            }
        }
    }

}
