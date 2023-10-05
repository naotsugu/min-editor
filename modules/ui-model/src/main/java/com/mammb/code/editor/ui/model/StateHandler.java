package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.model.text.LineEnding;
import com.mammb.code.editor.model.text.OffsetPoint;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public interface StateHandler {

    void addLineEndingChanged(Consumer<LineEnding> handler);
    void addCharsetChanged(Consumer<Charset> handler);
    void addCaretPointChanged(Consumer<OffsetPoint> handler);

}
