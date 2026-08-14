plugins {
    `java-library`
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
}

javafx {
    version = "26"
    modules("javafx.controls")
    configuration = "compileOnly"
}
