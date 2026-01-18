plugins {
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

javafx {
    version = "26-ea+22"
    modules("javafx.web")
}
