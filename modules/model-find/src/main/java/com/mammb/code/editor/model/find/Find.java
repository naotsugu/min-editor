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
package com.mammb.code.editor.model.find;

import com.mammb.code.editor.model.find.impl.FindImpl;
import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.text.OffsetPoint;
import java.util.function.Consumer;

/**
 * The Find.
 * @author Naotsugu Kobayashi
 */
public interface Find {

    /**
     * Run find.
     * @param base the base offset point
     * @param spec the find specification
     */
    void run(OffsetPoint base, FindSpec spec);

    /**
     * Cancel find.
     */
    void cancel();

    /**
     * Add a listener of find result.
     * @param listener a listener of find result
     */
    void addListener(Consumer<Found> listener);

    /**
     * Create a new find.
     * @param rowSupplier the row supplier
     * @return a new find
     */
    static Find of(RowSupplier rowSupplier) {
        return new FindImpl(rowSupplier);
    }

}
