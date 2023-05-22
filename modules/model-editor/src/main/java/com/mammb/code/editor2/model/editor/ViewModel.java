package com.mammb.code.editor2.model.editor;

import com.mammb.code.editor2.model.buffer.Buffers;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.style.StyledText;

public class ViewModel {

    private TextBuffer<StyledText> buffer;

    public ViewModel() {
        this.buffer = Buffers.of(25);
    }


}
