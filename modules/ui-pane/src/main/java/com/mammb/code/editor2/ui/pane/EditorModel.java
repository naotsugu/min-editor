package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor.javafx.layout.FxFontStyle;
import com.mammb.code.editor.javafx.layout.LayoutBuilder;
import com.mammb.code.editor2.model.buffer.Buffers;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.nio.file.Path;

public class EditorModel {

    private FxFontStyle fontStyle;
    private TextBuffer<TextLine> buffer;


    public EditorModel(double width, double height) {
        this(width, height, null);
    }


    public EditorModel(double width, double height, Path path) {
        fontStyle = FxFontStyle.of(Font.font(20));
        Translate<StyledText, Span> translate = in -> Span.of(in, fontStyle);
        buffer = Buffers.of(30, path, new LayoutBuilder(width), translate);
    }


    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);

        for (TextLine textLine : buffer.texts()) {
            for (TextRun run : textLine.runs()) {
                if (run.style() instanceof FxFontStyle fs) {
                    if (!gc.getFont().equals(fs.font())) {
                        gc.setFont(fontStyle.font());
                    }
                }
                gc.fillText(run.text(), run.layout().x(), run.layout().y());
            }
        }
    }


}
