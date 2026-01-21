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
 * The HunkGatherer.
 * @author Naotsugu Kobayashi
 */
public final class HunkGatherer {

    private HunkGatherer() {
        // This class is not intended to be instantiated
    }

    public static Gatherer<Integer, ?, Integer> of(int contextSize, int max) {
        return of(contextSize, max, null);
    }

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
