plugins {
    java
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

javafx {
    version = "20.0.1"
    modules("javafx.graphics")
}
