import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

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

val javafxControls: Configuration by configurations.creating
val javafxControlsSources: Configuration by configurations.creating

dependencies {
    javafxControls("org.openjfx:javafx-controls:21.0.1:${artifact}")
    javafxControlsSources("org.openjfx:javafx-controls:21.0.1:sources")
    implementation(project(":javafx"))
    implementation(project(":ui-pane"))
    implementation(project(":ui-prefs"))
}
