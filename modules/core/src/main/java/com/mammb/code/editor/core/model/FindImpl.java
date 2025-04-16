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

import com.mammb.code.editor.core.Find;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Point.*;
import com.mammb.code.piecetable.Pos;
import com.mammb.code.piecetable.SearchContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The find.
 * @author Naotsugu Kobayashi
 */
public class FindImpl implements Find {

    /** The search context. */
    private final SearchContext sc;

    FindImpl(SearchContext sc) {
        this.sc = sc;
    }

    @Override
    public List<PointLen> all(Spec spec) {
        if (spec.isEmpty()) return List.of();
        List<PointLen> list = new ArrayList<>();
        sc.findAll(spec(spec), seg -> seg.value().stream()
            .map(p -> PointLen.of(p.row(), p.col(), p.len())).forEach(list::add));
        return list;
    }

    @Override
    public Optional<PointLen> nextOne(Point base, Spec spec) {
        return sc.findOne(spec(spec), new Pos(base.row(), base.col()), SearchContext.Direction.FORWARD)
            .map(p -> PointLen.of(p.row(), p.col(), p.len()));
    }

    @Override
    public Optional<PointLen> prevOne(Point base, Spec spec) {
        return sc.findOne(spec(spec), new Pos(base.row(), base.col()), SearchContext.Direction.BACKWARD)
            .map(p -> PointLen.of(p.row(), p.col(), p.len()));
    }

    @Override
    public List<PointLen> founds() {
        return sc.founds().stream()
            .map(p -> PointLen.of(p.row(), p.col(), p.len())).toList();
    }

    @Override
    public Optional<PointLen> next(Point base) {
        return sc.next(new Pos(base.row(), base.col()), SearchContext.Direction.FORWARD)
            .map(p -> PointLen.of(p.row(), p.col(), p.len()));
    }

    @Override
    public Optional<PointLen> prev(Point base) {
        return sc.next(new Pos(base.row(), base.col()), SearchContext.Direction.BACKWARD)
            .map(p -> PointLen.of(p.row(), p.col(), p.len()));
    }

    @Override
    public void clear() {
        sc.clear();
    }

    private SearchContext.Spec spec(Spec spec) {
        return new SearchContext.Spec(
            spec.pattern(),
            switch (spec.patternType()) {
                case CASE_INSENSITIVE -> SearchContext.PatternCase.CASE_INSENSITIVE;
                case REGEX -> SearchContext.PatternCase.REGEX;
                default -> SearchContext.PatternCase.LITERAL;
            });
    }

}
