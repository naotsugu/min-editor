package com.mammb.code.editor.ui.app.control;

public class CssProcessor implements StringTemplate.Processor<CssRun, RuntimeException> {

    public static final CssProcessor CSS = new CssProcessor();

    @Override
    public CssRun process(StringTemplate template) throws RuntimeException {
        // pass through at the moment
        return new CssRun(StringTemplate.interpolate(template.fragments(), template.values()));
    }
}
