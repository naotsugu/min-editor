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

import com.mammb.code.editor2.model.buffer.Metrics;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditTo;
import com.mammb.code.editor2.model.text.OffsetPoint;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * MetricsImpl.
 * @author Naotsugu Kobayashi
 */
public class MetricsImpl implements Metrics, Consumer<byte[]> {

    /** The content path. */
    private Path path;
    /** The byte length of content. */
    private long byteLen = 0;
    /** The code point count. */
    private int cpCount = 0;
    /** The char count. */
    private int chCount = 0;
    /** The invalid code point count. */
    private int invalid = 0;
    /** The carriage return count. */
    private int crCount = 0;
    /** The line feed count. */
    private int lfCount = 0;
    /** whether dirty. */
    private boolean isDirty = false;

    /**
     * Constructor.
     * @param path the content path
     */
    public MetricsImpl(Path path) {
        this.path = path;
    }


    /**
     * Set the content path.
     * @param path the content path
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * Set the dirty.
     * @param dirty whether dirty
     */
    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    /**
     * Apply the edit.
     * @param edit the edit
     */
    public void apply(Edit edit) {
        edit.apply(editTo);
    }

    @Override
    public void accept(byte[] bytes) {
        plus(bytes);
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public long byteLen() {
        return byteLen;
    }

    @Override
    public int cpCount() {
        return cpCount;
    }

    @Override
    public int chCount() {
        return chCount;
    }

    @Override
    public int invalidCpCount() {
        return invalid;
    }

    @Override
    public int crCount() {
        return crCount;
    }

    @Override
    public int lfCount() {
        return lfCount;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetricsImpl.class.getSimpleName() + "[", "]")
            .add("path=" + path)
            .add("byteLen=" + byteLen)
            .add("cpCount=" + cpCount)
            .add("chCount=" + chCount)
            .add("invalid=" + invalid)
            .add("crCount=" + crCount)
            .add("lfCount=" + lfCount)
            .add("isDirty=" + isDirty)
            .toString();
    }

    // -- private -------------------------------------------------------------

    private void plus(byte[] bytes) {

        if (bytes == null || bytes.length == 0) return;

        for (int i = 0; i < bytes.length;) {
            byte b = bytes[i];
            if ((b & 0x80) == 0x00){
                if (b == '\r') crCount++;
                if (b == '\n') lfCount++;
                i += 1;
            } else if ((b & 0xE0) == 0xC0) {
                i += 2;
            } else if ((b & 0xF0) == 0xE0) {
                i += 3;
            } else if ((b & 0xF8) == 0xF0) {
                chCount++;
                i += 4;
            } else {
                invalid++;
                i += 1;
            }
            chCount++;
            cpCount++;
        }
        isDirty = true;
        byteLen += bytes.length;
    }

    private void minus(byte[] bytes) {

        if (bytes == null || bytes.length == 0) return;

        for (int i = 0; i < bytes.length;) {
            byte b = bytes[i];
            if ((b & 0x80) == 0x00){
                if (b == '\r') crCount--;
                if (b == '\n') lfCount--;
                i += 1;
            } else if ((b & 0xE0) == 0xC0) {
                i += 2;
            } else if ((b & 0xF0) == 0xE0) {
                i += 3;
            } else if ((b & 0xF8) == 0xF0) {
                chCount--;
                i += 4;
            } else {
                invalid--;
                i += 1;
            }
            chCount--;
            cpCount--;
        }
        isDirty = true;
        byteLen -= bytes.length;
    }

    private final EditTo editTo = new EditTo() {

        @Override
        public void insert(OffsetPoint point, String text) {
            plus(text.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void delete(OffsetPoint point, String text) {
            minus(text.getBytes(StandardCharsets.UTF_8));
        }
    };

}
