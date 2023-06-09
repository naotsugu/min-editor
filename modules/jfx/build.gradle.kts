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
val javafxGraphics: Configuration by configurations.creating

dependencies {
    javafxBase("org.openjfx:javafx-base:20.0.1:${artifact}")
    javafxGraphics("org.openjfx:javafx-graphics:20.0.1:${artifact}")
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
