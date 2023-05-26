plugins {
    `java-library`
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
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

group = "com.mammb.code"
