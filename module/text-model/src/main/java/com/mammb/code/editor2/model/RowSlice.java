package com.mammb.code.editor2.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RowSlice {

    private final List<PointText> list = new LinkedList<>();

    private RowSupplier rowSupplier;

    /** The row size of slice. */
    private int maxRowSize = 10;


    public RowSlice(int maxRowSize, RowSupplier rowSupplier) {
        pushEmptyIf();
        this.rowSupplier = Objects.requireNonNull(rowSupplier);
        this.maxRowSize = Math.max(maxRowSize, 1);
    }


    public void prev(int n) {
        for (int i = 0; i < n; i++) {
            PointText head = list.get(0);
            int cpOffset = head.point().cpOffset();
            if (cpOffset == 0) break;

            String str = rowSupplier.before(cpOffset);
            pushFirst(new PointText(head.point().minus(str), str));
        }
    }


    public void next(int n) {
        for (int i = 0; i < n; i++) {
            PointText tail = list.get(list.size() - 1);
            OffsetPoint next = tail.point().plus(tail.text());

            String str = rowSupplier.at(next.cpOffset());
            if (str == null) break;
            pushLast(new PointText(next, str));
        }
    }


    public void refresh(int rowNumber) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).point().row() >= rowNumber) {
                list.subList(i, list.size()).clear();
                break;
            }
        }
        fill();
    }


    public void clear() {
        list.clear();
        pushEmptyIf();
    }


    private void pushFirst(PointText... rows) {
        list.addAll(0, Arrays.asList(rows));
        while (list.size() > maxRowSize) {
            list.remove(list.size() - 1);
        }
    }


    private void pushLast(PointText... rows) {
        list.addAll(Arrays.asList(rows));
        while (list.size() > maxRowSize) {
            list.remove(0);
        }
    }


    public void fill() {
        pushEmptyIf();
        while (list.size() <= maxRowSize) {
            PointText tail = list.get(list.size() - 1);
            OffsetPoint next = tail.point().plus(tail.text());

            String str = rowSupplier.at(next.cpOffset());
            if (str == null) break;
            list.add(new PointText(next, str));
        }
    }


    private void pushEmptyIf() {
        if (list.isEmpty()) list.add(new PointText(OffsetPoint.zero, ""));
    }

}
