plugins {
    id("code.editor.base")
    application
}

dependencies {
    implementation(project(":ui-app"))
}

application {
    mainClass.set("com.mammb.code.editor.bootstrap.Main")
    mainModule.set("code.editor.bootstrap")
    if (providers.systemProperty("debug").isPresent) {
        applicationDefaultJvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
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
