plugins {
    `java-library`
    id("code.editor.base")
}

val os   = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
val arch = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentArchitecture()
val artifact = when {
    os.isMacOsX  && arch.isArm64 -> "mac-aarch64"
    os.isMacOsX  && arch.isAmd64 -> "mac"
    os.isLinux   && arch.isArm64 -> "linux-aarch64"
    os.isLinux   && arch.isAmd64 -> "linux"
    os.isWindows && arch.isAmd64 -> "win"
    else -> throw Error("Unsupported OS: $os, ARCH: $arch")
}

val javafxBase: Configuration by configurations.creating
val javafxBaseSources: Configuration by configurations.creating
val javafxGraphics: Configuration by configurations.creating
val javafxGraphicsSources: Configuration by configurations.creating

dependencies {
    javafxBase("org.openjfx:javafx-base:20.0.1:${artifact}")
    javafxBaseSources("org.openjfx:javafx-base:20.0.1:sources")
    javafxGraphics("org.openjfx:javafx-graphics:20.0.1:${artifact}")
    javafxGraphicsSources("org.openjfx:javafx-graphics:20.0.1:sources")
    api(files("${buildDir}/libs/javafx-base.jar"))
    api(files("${buildDir}/libs/javafx-graphics.jar"))
}

tasks.register<Jar>("baseJar") {
    archiveBaseName.set("javafx-base")
    from(javafxBase
        .filter { it.name.startsWith("javafx-base-") }
        .map { zipTree(it) }
    ) { exclude("module-info.class") }
}

tasks.register<Jar>("graphicsJar") {
    archiveBaseName.set("javafx-graphics")
    from(javafxGraphics
        .filter { it.name.startsWith("javafx-graphics-") }
        .map { zipTree(it) }
    ) { exclude("module-info.class") }
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn("baseJar")
    dependsOn("graphicsJar")
}


java.withSourcesJar()

tasks.register<Jar>("baseSourcesJar") {
    archiveBaseName.set("javafx-base-sources")
    from({ javafxBaseSources.map { zipTree(it) } })
}

tasks.register<Jar>("graphicsSourcesJar") {
    archiveBaseName.set("javafx-graphics-sources")
    from({ javafxGraphicsSources.map { zipTree(it) } })
}

tasks.named<Jar>("sourcesJar") {
    dependsOn("baseSourcesJar")
    dependsOn("graphicsSourcesJar")
}
