plugins {
    id("code.editor.base")
    application
    id("org.beryx.jlink") version "3.0.1"
}

dependencies {
    implementation(project(":ui-app"))
}

application {
    mainClass.set("com.mammb.code.editor.bootstrap.Main")
    mainModule.set("code.editor.bootstrap")
    applicationDefaultJvmArgs = listOf("--enable-preview")
    if (providers.systemProperty("debug").isPresent) {
        applicationDefaultJvmArgs = applicationDefaultJvmArgs.plus(listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"))
    }
}

tasks.register<Jar>("uberJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("min-editor")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    manifest {
        attributes("Main-Class" to "com.mammb.code.editor.bootstrap.Main")
    }
}

jlink {
    launcher {
        name = "min-editor"
        noConsole = true
    }
    enableCds()
}
