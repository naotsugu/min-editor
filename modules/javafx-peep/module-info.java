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
        javafx.web;
    exports com.sun.glass.utils to
        javafx.web;
    exports com.sun.javafx.application to
        java.base,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.css to
        javafx.controls;
    exports com.sun.javafx.font to
        code.editor.ui.fx,
        javafx.web;
    exports com.sun.javafx.geom to
        code.editor.ui.fx,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.geom.transform to
        code.editor.ui.fx,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.iio to
        javafx.web;
    exports com.sun.javafx.menu to
        javafx.controls;
    exports com.sun.javafx.scene to
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.scene.input to
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.scene.layout to
        code.editor.ui.fx,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.scene.text to
        code.editor.ui.fx,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.scene.traversal to
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.sg.prism to
        javafx.web;
    exports com.sun.javafx.stage to
        javafx.controls;
    exports com.sun.javafx.text to
        code.editor.ui.fx,
        javafx.web;
    exports com.sun.javafx.tk to
        code.editor.ui.fx,
        javafx.controls,
        javafx.web;
    exports com.sun.javafx.util to
        javafx.controls,
        javafx.web;
    exports com.sun.prism to
        javafx.web;
    exports com.sun.prism.image to
        javafx.web;
    exports com.sun.prism.paint to
        javafx.web;
    exports com.sun.scenario.effect to
        javafx.web;
    exports com.sun.scenario.effect.impl to
        javafx.web;
    exports com.sun.scenario.effect.impl.prism to
        javafx.web;
}
