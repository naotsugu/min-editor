/*
 * Copyright 2022-2024 the original author or authors.
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
package com.mammb.code.editor.core;

import com.mammb.code.editor.core.Point.Range;
import com.mammb.code.editor.core.model.TextEditContent;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The content.
 * @author Naotsugu Kobayashi
 */
public interface Content {

    Point insert(Point point, String text);
    List<Point> insert(List<Point> points, String text);
    String delete(Point point);
    List<Point> delete(List<Point> points);
    Point backspace(Point point);
    List<Point> backspace(List<Point> points);
    Point replace(Point start, Point end, String text);
    List<Point> replace(List<Range> ranges, Function<String, String> fun);

    /**
     * Undo.
     * @return the undo position
     */
    List<Point> undo();

    /**
     * Redo.
     * @return the redo position
     */
    List<Point> redo();

    String getText(int row);
    String getText(Point start, Point end);
    int rows();
    Optional<Path> path();
    void save(Path path);
    boolean isModified();
    Point insertFlush(Point point, String text);
    void clearFlush();
    List<Point> findAll(String text);

    static Content of() {
        return new TextEditContent();
    }
    static Content of(Path path) {
        return new TextEditContent(path);
    }


}
