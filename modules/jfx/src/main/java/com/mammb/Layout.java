package com.mammb;

public interface Layout {

    /**
     * Get the X coordinate of this {@code CoordPoint}.
     * @return the X coordinate of this {@code CoordPoint}
     */
    double x();

    /**
     * Get the Y coordinate of this {@code CoordPoint}.
     * @return the Y coordinate of this {@code CoordPoint}
     */
    double y();

    /**
     * The width of the {@code GlyphRun}.
     * @return the width of the {@code GlyphRun}
     */
    double width();

    /**
     * The height of the {@code GlyphRun}.
     * @return the height of the {@code GlyphRun}
     */
    double height();


    static Layout of(double x, double y, double width, double height) {
        record LayoutRecord(double x, double y, double width, double height) implements Layout { }
        return new LayoutRecord(x, y, width, height);
    }

}
