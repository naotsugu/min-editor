/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.core.tools;

import java.util.stream.Gatherer;

/**
 * A final class that serves as an implementation detail for gathering ranges
 * of integers within a defined context size and upper bound. Specifically, it
 * processes a sequence of integer inputs and integrates them into continuous
 * blocks of integers, optionally inserting a separator marker between distinct
 * gathered blocks.
 * <p>
 * Instances of this class are created indirectly via the {@code of} factory
 * methods and are used internally in conjunction with the {@code Gatherer}
 * framework.
 * @author Naotsugu Kobayashi
 */
public final class HunkGatherer {

    private final Integer separateMarker;
    private final int contextSize;
    private final int min = 0;
    private final int max;
    private Integer start = null;
    private Integer end = null;

    private HunkGatherer(int contextSize, int max, Integer separateMarker) {
        this.contextSize = contextSize;
        this.max = max;
        this.separateMarker = separateMarker;
    }

    /**
     * Creates a new {@code Gatherer} instance for managing a sequence of integers
     * within a specified context size and upper bound.
     *
     * @param contextSize the number of contextual elements to include on either side
     *                    of a central value within the range. Must be a positive value.
     * @param max the maximum value in the range of integers. Defines the upper limit
     *            for the gathered data.
     * @return a {@code Gatherer} instance configured to collect integers based on
     *         the provided context size and maximum value.
     */
    public static Gatherer<Integer, ?, Integer> of(int contextSize, int max) {
        return of(contextSize, max, null);
    }

    /**
     * Creates a new {@code Gatherer} instance tailored for processing a sequence of integers
     * within a specified context size and maximum value, with an optional separate marker
     * for distinguishing segments or groups.
     *
     * @param contextSize the number of contextual elements to include on either side of a
     *                    central value within the range. Must be a positive integer.
     * @param max the maximum value in the range of integers. Defines the highest limit
     *            for the gathered data.
     * @param separateMarker an optional marker used to separate or define discrete sections
     *                       within the gathered sequence. Can be {@code null} if not needed.
     * @return a {@code Gatherer} instance configured to collect integers based on the provided
     *         context size, maximum value, and separate marker.
     */
    public static Gatherer<Integer, ?, Integer> of(int contextSize, int max, Integer separateMarker) {
        return Gatherer.<Integer, HunkGatherer, Integer>ofSequential(
            () -> new HunkGatherer(contextSize, max, separateMarker),
            HunkGatherer::integrate,
            HunkGatherer::finish
        );
    }

    private boolean integrate(Integer line, Gatherer.Downstream<? super Integer> downstream) {

        int nextStart = Math.clamp(line - contextSize, min, max);
        int nextEnd   = Math.clamp(line + contextSize, min, max);

        if (start == null) {
            start = nextStart;
            end = nextEnd;
        } else if (nextStart <= end + 1) {
            end = Math.max(end, nextEnd);
        } else {
            emitBlock(downstream, start, end);
            start = nextStart;
            end = nextEnd;
        }
        return true;
    }

    private void finish(Gatherer.Downstream<? super Integer> downstream) {
        emitBlock(downstream, start, end);
    }

    private void emitBlock(Gatherer.Downstream<? super Integer> downstream, int start, int end) {
        if (contextSize > 0 && separateMarker != null) {
            downstream.push(separateMarker);
        }
        for (int i = start; i <= end; i++) {
            downstream.push(i);
        }
    }

}
