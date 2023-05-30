module javafx.graphics {
    requires java.desktop;
    requires java.xml;
    requires jdk.unsupported;

    requires transitive javafx.base;

    exports javafx.animation;
    exports javafx.application;
    exports javafx.concurrent;
    exports javafx.css;
    exports javafx.css.converter;
    exports javafx.geometry;
    exports javafx.print;
    exports javafx.scene;
    exports javafx.scene.canvas;
    exports javafx.scene.effect;
    exports javafx.scene.image;
    exports javafx.scene.input;
    exports javafx.scene.layout;
    exports javafx.scene.paint;
    exports javafx.scene.robot;
    exports javafx.scene.shape;
    exports javafx.scene.text;
    exports javafx.scene.transform;
    exports javafx.stage;

    exports com.sun.glass.ui to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.glass.utils to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.media,
        javafx.web;
    exports com.sun.javafx.application to
        code.editor.javafx,
        code.editor.javafx.shim,
        java.base,
        javafx.controls,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.css to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls;
    exports com.sun.javafx.cursor to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.swing;
    exports com.sun.javafx.embed to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.swing;
    exports com.sun.javafx.font to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.javafx.geom to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.geom.transform to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.iio to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.javafx.menu to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls;
    exports com.sun.javafx.scene to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.scene.input to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.scene.layout to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.scene.text to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.scene.traversal to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.sg.prism to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.stage to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.swing;
    exports com.sun.javafx.text to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.javafx.tk to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.javafx.util to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.controls,
        javafx.fxml,
        javafx.media,
        javafx.swing,
        javafx.web;
    exports com.sun.prism to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.media,
        javafx.web;
    exports com.sun.prism.image to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.prism.paint to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.scenario.effect to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.scenario.effect.impl to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
    exports com.sun.scenario.effect.impl.prism to
        code.editor.javafx,
        code.editor.javafx.shim,
        javafx.web;
}
