package com.mammb.code.editor2.model;

/**
 * OffsetPoint.
 * <pre>
 * 1: |a|b|c|    OffsetPoint(0, 0, 0)
 * 2: |d|e|f|    OffsetPoint(1, 3, 3)
 *    |g|😀|     OffsetPoint(1, 6, 6)
 * 3: |i|j|k|    OffsetPoint(2, 9, 8)
 * </pre>
 *
 * @param row the number of row(zero based)
 * @param offset the offset of content(char base)
 * @param cpOffset the code point offset of content
 */
public record OffsetPoint(int row, int offset, int cpOffset) {

    /** zero. */
    public static OffsetPoint zero = new OffsetPoint(0, 0, 0);


    public OffsetPoint plus(String str) {
        return new OffsetPoint(
            row + countRow(str),
            offset + str.length(),
            cpOffset + Character.codePointCount(str, 0, str.length()));
    }


    public OffsetPoint minus(String str) {
        return new OffsetPoint(
            row - countRow(str),
            offset - str.length(),
            cpOffset - Character.codePointCount(str, 0, str.length()));
    }


    private static int countRow(CharSequence cs) {
        return (cs == null) ? 0 : (int) cs.chars().filter(c -> c == '\n').count();
    }

}
