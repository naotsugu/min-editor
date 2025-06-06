plugins {
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

javafx {
    version = "24.0.1"
    modules("javafx.web")
}
