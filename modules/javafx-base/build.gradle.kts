plugins {
    java
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
    modularity.inferModulePath.set(false)
}

tasks.compileJava {
    modularity.inferModulePath.set(false)
}

javafx {
    version = "20"
    modules("javafx.base")
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filter { it.name.startsWith("javafx-base") }
            .map { zipTree(it) }
    })
    from(sourceSets.main.get().output)
}

