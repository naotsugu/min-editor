package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor.javafx.layout.FxLayoutBuilder;
import com.mammb.code.editor.javafx.layout.SpanTranslate;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.LayoutWrapTranslate;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import java.util.List;

public class WrapScreen {

    private final TextBuffer<Textual> editBuffer;

    private final Translate<Textual, List<TextLine>> translator;

    private FxLayoutBuilder layout;

    public WrapScreen(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
        this.translator = translator();
        this.layout = new FxLayoutBuilder();
    }

    private Translate<Textual, List<TextLine>> translator() {
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutWrapTranslate.of(layout));
    }

}
