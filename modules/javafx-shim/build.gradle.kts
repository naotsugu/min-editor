plugins {
    id("code.editor.base")
}

tasks.build {
    dependsOn(gradle.includedBuild("javafx").task(":build"))
}

dependencies {
    api("com.mammb.code:javafx")
    api(project(":model-layout"))
}
