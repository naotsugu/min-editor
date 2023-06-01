plugins {
    `java-library`
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

val copySources: Configuration by configurations.creating
dependencies {
    copySources("org.openjfx:javafx-graphics:20:sources")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
    withSourcesJar()
    modularity.inferModulePath.set(false)
}

javafx {
    version = "20"
    modules("javafx.graphics")
}

tasks.compileJava {
    modularity.inferModulePath.set(false)
}


tasks.named<Jar>("jar") {
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filter { it.name.startsWith("javafx-graphics") }
            .map { zipTree(it) }
    }) {
        exclude("module-info.class")
    }
    exclude("**/dummy.class")
}

tasks.named<Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({ copySources.map { zipTree(it) } })
}

group = "com.mammb.code"

tasks.register("initModule") {
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
tasks.named<JavaCompile>("compileJava") {
    dependsOn("initModule")
}
