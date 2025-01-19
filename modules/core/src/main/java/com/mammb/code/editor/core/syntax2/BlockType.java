package com.mammb.code.editor.core.syntax2;

import java.util.function.Function;

public interface BlockType {

    String open();

    String close();

    interface BlockTypeWith<T> extends BlockType, With<T> {
        Function<String, T> withSupply();
    }

    interface Range extends BlockType { }
    interface Neutral extends BlockType {
        default String close() { return open(); }
    }
    interface NeutralWith<T> extends Neutral, BlockTypeWith<T> {
        Function<String, T> withSupply();
    }

    static Range range(String open, String close) {
        record RangeRecord(String open, String close) implements Range {}
        return new RangeRecord(open, close);
    }

    static Neutral neutral(String open) {
        record NeutralRecord(String open) implements Neutral {}
        return new NeutralRecord(open);
    }

    static <T> NeutralWith<T> neutral(String open, Function<String, T> withSupply) {
        record NeutralWithRecord<T>(String open, Function<String, T> withSupply) implements NeutralWith<T> {}
        return new NeutralWithRecord<>(open, withSupply);
    }

}
