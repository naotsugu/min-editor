package com.mammb.code.editor.core.syntax2;

import java.util.Objects;

public class LexerSource {
    private int row;
    private String text;
    private int index = 0;
    private int peek = 0;

    private LexerSource(int row, String text) {
        this.row = row;
        this.text = text;
    }

    public static LexerSource of(int row, String source) {
        return new LexerSource(row, source);
    }

    public int row() { return row; }
    public String text() { return text; }
    public int length() { return text.length(); }

    public boolean hasNext() {
        return index < text.length();
    }


    public Indexed peek() {
        var ret = new Indexed(index + peek, text.charAt(index + peek), text.length());
        peek++;
        return ret;
    }

    public LexerSource commitPeek() {
        index += peek;
        peek = 0;
        return this;
    }

    public boolean match(CharSequence cs) {
        return index + cs.length() < text.length() &&
            Objects.equals(text.substring(index, index + cs.length()), cs.toString());
    }

    public record Indexed(int index, String string, int parentLength) {
        private Indexed(int index, char ch, int parentLength) {
            this(index, String.valueOf(ch), parentLength);
        }
        char ch() {
            return length() == 0 ? 0 : string.charAt(0);
        }
        int lastIndex() {
            return index + string.length() - 1;
        }
        int length() { return string.length(); }
        boolean isFirst() { return index == 0; }
        boolean isLast() { return index == parentLength - 1; }
    }
}
