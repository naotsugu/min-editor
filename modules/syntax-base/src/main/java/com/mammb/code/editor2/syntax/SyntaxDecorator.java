package com.mammb.code.editor2.syntax;

import com.mammb.code.editor2.model.core.Decorate;
import com.mammb.code.editor2.model.core.PointText;

import java.util.List;

public class SyntaxDecorator implements Decorate<PointText, PointText> {

    @Override
    public List<PointText> apply(List<PointText> texts) {
        return texts;
    }

}
