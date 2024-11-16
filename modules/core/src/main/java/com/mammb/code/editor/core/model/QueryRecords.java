package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Query;

public interface QueryRecords {
    record RowEndingSymbol() implements Query<String> { }
    record CharsetSymbol() implements Query<String> { }
    record Modified() implements Query<Boolean> {}
    record Bom() implements Query<byte[]> {}
    record CaretPoint() implements Query<Point> {}
}
