package com.mammb.code.editor2.ui.pane;

public class Caret {

    /** The caret offset */
    private int charOffset = 0;
    /** The caret position x. */
    private double x = 0;
    /** The caret position y. */
    private double y = 0;
    /** The caret height. */
    private double height = 0;
    /** The logical caret position x. */
    private double logicalX = 0;

    public void moveTo(double x, double y, double height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public void moveTo(int charOffset) {
        this.charOffset = charOffset;
    }


    public Caret copy() {
        Caret copy = new Caret();
        copy.charOffset = charOffset;
        copy.x = x;
        copy.y = y;
        copy.height = height;
        copy.logicalX = logicalX;
        return copy;
    }

    public void plusOffset() {
        charOffset++;
    }

    public void minusOffset() {
        charOffset--;
    }

    public void syncLogical() {
        logicalX = x;
    }

    public int charOffset() {
        return charOffset;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double y2() {
        return y + height;
    }

    public double height() {
        return height;
    }

    public double logicalX() {
        return logicalX;
    }

}
