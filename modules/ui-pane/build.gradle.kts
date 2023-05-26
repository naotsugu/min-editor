plugins {
    id("code.editor.base")
}

tasks.build {
    dependsOn(gradle.includedBuild("javafx").task(":build"))
}

dependencies {
    implementation("com.mammb.code:javafx")
    implementation(project(":model-editor"))
}
