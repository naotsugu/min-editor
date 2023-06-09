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
package com.mammb.code.editor2.model.buffer.impl;

import com.mammb.code.editor2.model.buffer.Content;
import com.mammb.code.editor2.model.text.LineEnding;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.piecetable.PieceTable;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Content.
 * @author Naotsugu Kobayashi
 */
public class PtContent implements Content {

    /** The content path. */
    private Path path;

    /** The line ending. */
    private LineEnding lineEnding;

    /** The piece table. */
    private final PieceTable pt;


    /**
     * Constructor.
     */
    public PtContent() {
        this(LineEnding.platform());
    }

    /**
     * Constructor.
     * @param lineEnding the line ending
     */
    public PtContent(LineEnding lineEnding) {
        this.pt = PieceTable.of("");
        this.lineEnding = lineEnding;
    }

    /**
     * Create content for specified path.
     * @param path the content path
     */
    public PtContent(Path path) {
        this.pt = PieceTable.of(path);
        this.path = path;
        this.lineEnding = LineEnding.LF; // TODO
    }

    @Override
    public byte[] bytes(int startPos, Predicate<byte[]> until) {
        return pt.bytes(startPos, until);
    }

    @Override
    public byte[] bytesBefore(int startPos, Predicate<byte[]> until) {
        return pt.bytesBefore(startPos, until);
    }

    @Override
    public void insert(OffsetPoint point, CharSequence cs) {
        pt.insert(point.cpOffset(), cs);
    }

    @Override
    public void delete(OffsetPoint point, int len) {
        pt.delete(point.cpOffset(), len);
    }

    @Override
    public void save() {
        pt.write(path);
    }

    @Override
    public void saveAs(Path path) {
        pt.write(path);
        this.path = path;
    }

}
