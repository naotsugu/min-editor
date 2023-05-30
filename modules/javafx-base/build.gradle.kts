plugins {
    `java-library`
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

val copySources: Configuration by configurations.creating
dependencies {
    copySources("org.openjfx:javafx-base:20:sources")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
    withSourcesJar()
}

javafx {
    version = "20"
    modules("javafx.base")
}

tasks.named<Jar>("jar") {
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filter { it.name.startsWith("javafx-base") }
            .map { zipTree(it) }
    })
}

tasks.named<Jar>("sourcesJar") {
    from({ copySources.map { zipTree(it) } })
}

group = "com.mammb.code"
