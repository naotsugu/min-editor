import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;

plugins {
    id("code.editor.base")
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

val javafxGraphics: Configuration by configurations.creating
val javafxBase: Configuration by configurations.creating

val javafxGraphicsSources: Configuration by configurations.creating
val javafxBaseSources: Configuration by configurations.creating

dependencies {
    javafxGraphics("org.openjfx:javafx-graphics:20.0.1:${artifact}")
    javafxBase("org.openjfx:javafx-base:20.0.1:${artifact}")

    javafxGraphicsSources("org.openjfx:javafx-graphics:20.0.1:sources")
    javafxBaseSources("org.openjfx:javafx-base:20.0.1:sources")
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

tasks.register<Jar>("graphicsSourcesJar") {
    archiveBaseName.set("javafx-graphics-sources")
    from({ javafxGraphicsSources.map { zipTree(it) } })
}
tasks.register<Jar>("baseSourcesJar") {
    archiveBaseName.set("javafx-base-sources")
    from({ javafxBaseSources.map { zipTree(it) } })
}

tasks.assemble {
    dependsOn("graphicsJar")
    dependsOn("baseJar")
    dependsOn("graphicsSourcesJar")
    dependsOn("baseSourcesJar")
}
