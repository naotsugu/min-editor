import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;

plugins {
    id("buildlogic.base")
    application
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
    implementation(project(":platform"))
}

application {
    mainClass = "com.mammb.code.editor.bootstrap.Main"
    mainModule = "code.editor.bootstrap"
    applicationDefaultJvmArgs = listOf(
        "-Xms32m",
        "--enable-preview",
        "-XX:+UseZGC", "-XX:+ZUncommit", "-XX:ZUncommitDelay=64m",
        //"-XX:+UseCompactObjectHeaders",
        "--enable-native-access=javafx.graphics") // Restricted methods will be blocked in a future release unless native access is enabled
        //"-XX:G1PeriodicGCInterval=5000")
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

tasks.register<Exec>("jpackage") {

    dependsOn(tasks.jar)

    val javaToolchainService = project.extensions.getByType(JavaToolchainService::class.java)
    val jdkPath = javaToolchainService.launcherFor(java.toolchain).get().executablePath
    val commandPath = File(jdkPath.asFile.parentFile, "jpackage").absolutePath

    val outputDir = project.layout.buildDirectory.dir("jpackage").get().asFile
    doFirst { delete(outputDir) }

    val jarFileProvider = tasks.named<Jar>("jar").flatMap { it.archiveFile }
    val modulePath = configurations.runtimeClasspath.get().joinToString(separator = File.pathSeparator) {
        it.absolutePath
    }

    val iconType = if (os.isWindows) "icon.ico" else if (os.isMacOsX) "icon.icns" else "icon.png"
    val iconPath = "${project.rootDir}/docs/icon/${iconType}"

    commandLine(commandPath,
        "--type", "app-image",
        "--name", "min-editor",
        "--module-path", jarFileProvider.get().asFile.absolutePath,
        "--module-path", modulePath,
        "--module", "${application.mainModule.get()}/${application.mainClass.get()}",
        "--dest", outputDir.absolutePath,
        "--icon", iconPath,

        "--jlink-options", "--strip-debug",
        "--jlink-options", "--compress=zip-0",
        "--jlink-options", "--no-header-files",
        "--jlink-options", "--no-man-pages",

        "--java-options", "-Xms32m",
        "--java-options", "--enable-preview",
        "--java-options", "-XX:+UseZGC",
        "--java-options", "-XX:+ZUncommit",
        "--java-options", "-XX:ZUncommitDelay=64m",
        //"--java-options", "-XX:+UseCompactObjectHeaders",
        "--java-options", "--enable-native-access=javafx.graphics", // Restricted methods will be blocked in a future release unless native access is enabled
    )
}

tasks.register<Zip>("pkg") {
    dependsOn("jpackage")
    isPreserveFileTimestamps = true
    isReproducibleFileOrder = false
    useFileSystemPermissions()
    archiveFileName = "min-editor-${platform}.zip"
    from(layout.buildDirectory.dir("jpackage"))
}
