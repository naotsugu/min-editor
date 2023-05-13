plugins {
    id("code.editor.base")
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

dependencies {
    implementation(project(":piecetable"))
}

application {
    mainClass.set("com.mammb.code.editor.App")
    mainModule.set("com.mammb.code.editor")
}

javafx {
    version = "20"
    modules("javafx.controls")
}
