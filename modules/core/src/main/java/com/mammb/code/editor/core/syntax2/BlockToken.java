package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.syntax2.BlockType.BlockTypeWith;

public interface BlockToken {

    BlockType type();

    /**
     * Composite interface {@link With}.
     * @param <T>
     */
    interface BlockTokenWith<T> extends BlockToken, With<T> {
        T with();
    }

    interface Open extends BlockToken {}
    interface Close extends BlockToken {}
    interface OpenWith<T> extends Open, BlockTokenWith<T> {}

    static Open open(BlockType type) {
        record OpenRecord(BlockType type) implements Open {}
        return new OpenRecord(type);
    }

    static Close close(BlockType type) {
        record CloseRecord(BlockType type) implements Close {}
        return new CloseRecord(type);
    }

    static <T> OpenWith<T> open(BlockTypeWith<T> type, T with) {
        record OpenWithRecord<T>(BlockTypeWith<T> type, T with) implements OpenWith<T> {}
        return new OpenWithRecord<>(type, with);
    }


}
