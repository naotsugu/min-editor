package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Query;

public interface QueryRecords {
    record RowEndingSymbol() implements Query<String> { }
    record CharsetSymbol() implements Query<String> { }
    record Modified() implements Query<Boolean> {}
}
