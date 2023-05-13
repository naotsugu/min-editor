package com.mammb.code.editor2.model;

public interface RowSupplier {

    String at(int cpOffset);

    String before(int cpOffset);

}
