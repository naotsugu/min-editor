plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

java {
    toolchain {
        // Gradle 8.1 is not support java 20
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
