import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;

plugins {
    id("buildlogic.base")
}

val os   = DefaultNativePlatform.getCurrentOperatingSystem()
val arch = DefaultNativePlatform.getCurrentArchitecture()
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
val javafxControls: Configuration by configurations.creating
val javafxWeb: Configuration by configurations.creating

val javafxBaseSources: Configuration by configurations.creating
val javafxGraphicsSources: Configuration by configurations.creating
val javafxControlsSources: Configuration by configurations.creating
val javafxWebSources: Configuration by configurations.creating

dependencies {
    javafxBase("org.openjfx:javafx-base:24.0.1:${artifact}")
    javafxGraphics("org.openjfx:javafx-graphics:24.0.1:${artifact}")
    javafxControls("org.openjfx:javafx-controls:24.0.1:${artifact}")
    javafxWeb("org.openjfx:javafx-web:24.0.1:${artifact}")

    javafxBaseSources("org.openjfx:javafx-base:24.0.1:sources")
    javafxGraphicsSources("org.openjfx:javafx-graphics:24.0.1:sources")
    javafxControlsSources("org.openjfx:javafx-controls:24.0.1:sources")
    javafxWebSources("org.openjfx:javafx-web:24.0.1:sources")
}


tasks.register<Copy>("createDummy") {
    from(file("module-info.java"))
    into(file("src/main/java"))
    doLast {
        val regex = """exports (.+)[;| ]""".toRegex()
        regex.findAll(file("src/main/java/module-info.java").readText()).forEach {
            val name = it.groups[1]?.value
            val path = "src/main/java/${name?.replace('.','/')}/"
            file(path).mkdirs()
            file("$path/dummy.java").writeText("""
                package $name;
                public class dummy {}
                """.trimIndent())
        }
    }
}
tasks.register<Delete>("cleanDummy") {
    delete(file("src"))
}
tasks.named<JavaCompile>("compileJava") {
    classpath += javafxBase
    dependsOn("createDummy")
    finalizedBy("cleanDummy")
}


tasks.register<Jar>("graphicsJar") {
    archiveBaseName.set("javafx-graphics")
    dependsOn(configurations.runtimeClasspath)
    from({ javafxGraphics
        .filter { it.name.endsWith("jar") }
        .filter { it.name.startsWith("javafx-graphics") }
        .map { zipTree(it) }
    }) {
        exclude("module-info.class")
    }
    from(sourceSets.main.get().output) {
        exclude("**/dummy.class")
    }
    mustRunAfter("jar")
}

tasks.register<Jar>("baseJar") {
    archiveBaseName.set("javafx-base")
    from({ javafxBase
        .filter { it.name.endsWith("jar") }
        .filter { it.name.startsWith("javafx-base-") }
        .map { zipTree(it) }
    })
}

tasks.register<Jar>("controlsJar") {
    archiveBaseName.set("javafx-controls")
    from({ javafxControls
        .filter { it.name.endsWith("jar") }
        .filter { it.name.startsWith("javafx-controls-") }
        .map { zipTree(it) }
    })
}

tasks.register<Jar>("webJar") {
    archiveBaseName.set("javafx-web")
    from({ javafxControls
        .filter { it.name.endsWith("jar") }
        .filter { it.name.startsWith("javafx-web-") }
        .map { zipTree(it) }
    })
}

tasks.register<Jar>("graphicsSourcesJar") {
    archiveBaseName.set("javafx-graphics-sources")
    from({ javafxGraphicsSources.map { zipTree(it) } })
}
tasks.register<Jar>("baseSourcesJar") {
    archiveBaseName.set("javafx-base-sources")
    from({ javafxBaseSources.map { zipTree(it) } })
}
tasks.register<Jar>("controlsSourcesJar") {
    archiveBaseName.set("javafx-controls-sources")
    from({ javafxControlsSources.map { zipTree(it) } })
}
tasks.register<Jar>("webSourcesJar") {
    archiveBaseName.set("javafx-web-sources")
    from({ javafxWebSources.map { zipTree(it) } })
}

tasks.assemble {
    dependsOn("graphicsJar")
    dependsOn("baseJar")
    dependsOn("controlsJar")
    dependsOn("webJar")
    dependsOn("graphicsSourcesJar")
    dependsOn("baseSourcesJar")
    dependsOn("controlsSourcesJar")
    dependsOn("webSourcesJar")
}
