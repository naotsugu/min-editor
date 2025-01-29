import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;

plugins {
    id("buildlogic.base")
    application
    id("org.beryx.jlink") version "3.1.1"
}


val os   = DefaultNativePlatform.getCurrentOperatingSystem()
val arch = DefaultNativePlatform.getCurrentArchitecture()
val platform = when {
    os.isMacOsX  && arch.isArm64 -> "mac-aarch64"
    os.isMacOsX  && arch.isAmd64 -> "mac"
    os.isLinux   && arch.isArm64 -> "linux-aarch64"
    os.isLinux   && arch.isAmd64 -> "linux"
    os.isWindows && arch.isAmd64 -> "win"
    else -> throw Error("Unsupported OS: $os, ARCH: $arch")
}

dependencies {
    implementation(project(":ui-fx"))
}

application {
    mainClass = "com.mammb.code.editor.bootstrap.Main"
    mainModule = "code.editor.bootstrap"
    applicationDefaultJvmArgs = listOf("-Xms64m", "--enable-preview")
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
    options = listOf("--strip-debug", "--compress", "zip-0", "--no-header-files", "--no-man-pages", "--add-modules", "jdk.charsets")
    // enableCds()
    launcher {
        name = "min-editor"
        noConsole = true
    }
    jpackage {
        imageName = "min-editor"
        val iconType = if (os.isWindows) "icon.ico" else if (os.isMacOsX) "icon.icns" else "icon.png"
        icon = "${project.rootDir}/docs/icon/${iconType}"
    }
}

tasks.register<Zip>("pkg") {
    dependsOn("jpackageImage")
    archiveFileName = "min-editor-${platform}.zip"
    from(layout.buildDirectory.dir("jpackage"))
}
