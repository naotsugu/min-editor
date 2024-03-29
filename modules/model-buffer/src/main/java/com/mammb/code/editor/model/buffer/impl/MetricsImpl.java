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
package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.MetricsChangeListener;
import com.mammb.code.editor.model.buffer.MetricsRecord;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditTo;
import com.mammb.code.editor.model.text.OffsetPoint;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * MetricsImpl.
 * @author Naotsugu Kobayashi
 */
public class MetricsImpl implements Metrics, Consumer<byte[]> {

    /** The content path. */
    private Path path;
    /** The charset. */
    private Charset charset;
    /** The byte length of content. */
    private long byteLen = 0;
    /** The code point count. */
    private long cpCount = 0;
    /** The char count. */
    private long chCount = 0;
    /** The invalid code point count. */
    private long invalid = 0;
    /** The carriage return count. */
    private int crCount = 0;
    /** The line feed count. */
    private int lfCount = 0;
    /** whether modified. */
    private boolean modified = false;

    /** The offset anchor. */
    private OffsetAnchor anchor = new OffsetAnchor();

    /** The metrics change listener. */
    private final List<MetricsChangeListener> changeListeners = new ArrayList<>();
    /** The invalid code detection listener. */
    private final List<MetricsChangeListener> invalidListeners = new ArrayList<>();



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
        handleChange(() -> this.path = path);
    }


    /**
     * Set the modified.
     * @param modified whether modified
     */
    public void setModified(boolean modified) {
        handleChange(() -> this.modified = modified);
    }


    /**
     * Set the charset.
     * @param charset the charset
     */
    public void setCharset(Charset charset) {
        handleChange(() -> this.charset = charset);
    }


    /**
     * Apply the edit.
     * @param edit the edit
     */
    public void apply(Edit edit) {
        handleChange(() -> edit.apply(editTo));
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
    public Charset charset() {
        return charset;
    }

    @Override
    public long byteLen() {
        return byteLen;
    }

    @Override
    public long cpCount() {
        return cpCount;
    }

    @Override
    public long chCount() {
        return chCount;
    }

    @Override
    public long invalidCpCount() {
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
    public boolean modified() {
        return modified;
    }

    @Override
    public OffsetPoint anchorPoint(int row) {
        return anchor.closestAnchorPoint(row);
    }

    @Override
    public void addChangeListener(MetricsChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(MetricsChangeListener listener) {
        changeListeners.remove(listener);
    }

    public void addInvalidListener(MetricsChangeListener listener) {
        invalidListeners.add(listener);
    }

    public void removeInvalidListener(MetricsChangeListener listener) {
        invalidListeners.remove(listener);
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
            .add("modified=" + modified)
            .toString();
    }

    // -- private -------------------------------------------------------------

    private void handleChange(Runnable runnable) {
        Metrics old = new MetricsRecord(this);
        runnable.run();
        changeListeners.forEach(l -> l.changed(old, new MetricsRecord(this)));
    }


    private void plus(byte[] bytes) {

        if (bytes == null || bytes.length == 0) return;

        for (int i = 0; i < bytes.length;) {
            byte b = bytes[i];
            if ((b & 0x80) == 0x00) {
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
                Metrics old = new MetricsRecord(this);
                invalid++;
                invalidListeners.forEach(l -> l.changed(old, new MetricsRecord(this)));
                i += 1;
            }
            chCount++;
            cpCount++;
            anchor.put(this);
        }
        modified = true;
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
        modified = true;
        byteLen -= bytes.length;
    }


    private final EditTo editTo = new EditTo() {

        @Override
        public void insert(OffsetPoint point, String text) {
            Metrics old = new MetricsRecord(MetricsImpl.this);
            plus(text.getBytes(StandardCharsets.UTF_8));
            applyAnchor(point, old);
        }

        @Override
        public void delete(OffsetPoint point, String text) {
            Metrics old = new MetricsRecord(MetricsImpl.this);
            minus(text.getBytes(StandardCharsets.UTF_8));
            applyAnchor(point, old);
        }

        private void applyAnchor(OffsetPoint point, Metrics old) {
            anchor.edited(point.cpOffset(),
                rowCount() - old.rowCount(),
                chCount - old.chCount(),
                cpCount - old.cpCount());
        }
    };

}
