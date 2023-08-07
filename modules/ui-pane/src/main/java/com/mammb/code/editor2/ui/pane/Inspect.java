package com.mammb.code.editor2.ui.pane;

import java.nio.file.Path;

public record Inspect(
    Path path,
    long byteLen,
    int cpCount,
    int chCount,
    int invalidCpCount,
    int crCount,
    int lfCount
    ) {
}
