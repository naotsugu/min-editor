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
 * Utility class for creating and managing {@code Gatherer} instances tailored for
 * collecting sequences of integers within a specified range and context size.
 * The {@code HunkGatherer} provides static methods to generate {@code Gatherer}
 * configurations and process integer sequences effectively. This class ensures that
 * integers are collected with a definable context size, optional delimiter marker,
 * and upper bound limit.
 * <p>
 * The resulting {@code Gatherer} can be used to process series of integers and
 * aggregate them into coherent blocks based on the specified context size.
 * Additionally, an optional separator marker can be inserted between ranges
 * to signal the boundary between distinct groups of integers.
 * @author Naotsugu Kobayashi
 */
public final class HunkGatherer {

    private HunkGatherer() {
        // This class is not intended to be instantiated
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
     * Creates a new {@code Gatherer} instance for managing a sequence of integers
     * within a specified context size and upper bound. Optionally, a separate
     * marker can be provided to insert a delimiter between distinct ranges of
     * integers.
     *
     * @param contextSize the number of contextual elements to include on either side
     *                    of a central value within the range. Must be a positive value.
     * @param max the maximum value in the range of integers. Defines the upper limit
     *            for the gathered data.
     * @param separateMarker an optional marker value to be inserted between separate
     *                       blocks of collected integers. Can be {@code null}.
     * @return a {@code Gatherer} instance configured to collect integers based on
     *         the provided context size, maximum value, and optional separate marker.
     */
    public static Gatherer<Integer, ?, Integer> of(int contextSize, int max, Integer separateMarker) {
        return Gatherer.<Integer, State, Integer>ofSequential(
            () -> new State(contextSize, max, separateMarker),
            HunkGatherer::integrate,
            HunkGatherer::finish
        );
    }

    private static class State {
        final Integer separateMarker;
        final int contextSize;
        Integer start = null;
        Integer end = null;
        final int min = 0;
        final int max;
        State(int contextSize, int max, Integer separateMarker) {
            this.contextSize = contextSize;
            this.max = max;
            this.separateMarker = separateMarker;
        }
    }

    private static boolean integrate(State state, Integer line, Gatherer.Downstream<? super Integer> downstream) {
        int nextStart = Math.clamp(line - state.contextSize, state.min, state.max);
        int nextEnd   = Math.clamp(line + state.contextSize, state.min, state.max);

        if (state.start == null) {
            state.start = nextStart;
            state.end = nextEnd;
            return true;        }

        if (nextStart <= state.end + 1) {
            state.end = Math.max(state.end, nextEnd);
        } else {
            emitBlock(state, downstream, state.start, state.end);
            state.start = nextStart;
            state.end = nextEnd;
        }
        return true;
    }

    private static void finish(State state, Gatherer.Downstream<? super Integer> downstream) {
        if (state.start != null) {
            emitBlock(state, downstream, state.start, state.end);
        }
    }

    private static void emitBlock(State state, Gatherer.Downstream<? super Integer> downstream, int start, int end) {
        if (state.contextSize > 0 && state.separateMarker != null) {
            downstream.push(state.separateMarker);
        }
        for (int i = start; i <= end; i++) {
            downstream.push(i);
        }
    }

}
