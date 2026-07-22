plugins {
    `java-library`
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

javafx {
    version = "26"
    modules("javafx.controls")
    configuration = "compileOnly"
}
