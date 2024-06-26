/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.model.content.impl;

import com.mammb.code.editor.model.content.Content;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.piecetable.PieceTable;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Content.
 * @author Naotsugu Kobayashi
 */
public class PtContent implements Content {

    /** The content path. */
    private Path path;

    /** The piece table. */
    private PieceTable pt;


    /**
     * Constructor.
     */
    public PtContent() {
        this.pt = PieceTable.of("");
    }


    /**
     * Create content for specified path.
     * @param path the content path
     * @param traverse the bytes traverse at initial loading
     */
    public PtContent(Path path, Consumer<byte[]> traverse) {
        this.pt = PieceTable.of(path, traverse);
        this.path = path;
    }


    @Override
    public byte[] bytes(long startPos, Predicate<byte[]> until) {
        return pt.bytes(startPos, until);
    }

    @Override
    public byte[] bytesBefore(long startPos, Predicate<byte[]> until) {
        return pt.bytesBefore(startPos, until);
    }

    @Override
    public long offset(long startCpOffset, Predicate<byte[]> until) {
        return pt.position(startCpOffset, until);
    }

    @Override
    public long offsetBefore(long startCpOffset, Predicate<byte[]> until) {
        return pt.positionBefore(startCpOffset, until);
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
        pt = PieceTable.of(path);
    }

    @Override
    public void saveAs(Path path) {
        pt.write(path);
        pt = PieceTable.of(path);
        this.path = path;
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public long length() {
        return pt.length();
    }

}
