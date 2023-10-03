plugins {
    java
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

javafx {
    version = "20.0.2"
    modules("javafx.controls")
}
