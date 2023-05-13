plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "min-editor"
// main module
include("app")

// git submodule
include(":piecetable")
project(":piecetable").projectDir = file("module/piecetable/lib")

// other module
include("javafx", "text-model")
for (project in rootProject.children.filterNot { it.name == "app" || it.name == "piecetable" }) {
    project.projectDir = file("module/${project.name}")
}
