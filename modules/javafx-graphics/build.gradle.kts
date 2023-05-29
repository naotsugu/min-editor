plugins {
    `java-library`
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

val copySources: Configuration by configurations.creating

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    copySources("org.openjfx:javafx-graphics:20:sources")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
    withSourcesJar()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

javafx {
    version = "20"
    modules("javafx.graphics")
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filter { it.name.startsWith("javafx-graphics") }
            .map { zipTree(it) }
    })
    from(sourceSets.main.get().output)
}

tasks.named<Jar>("sourcesJar") {
    from({
        copySources.map { zipTree(it) }
    })
}

group = "com.mammb.code"
