plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.openrewrite.rewrite:org.openrewrite.rewrite.gradle.plugin:7.23.0")
}
