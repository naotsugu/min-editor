plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "min-editor"

// main module
//include("app")
include("bootstrap")

// git submodule
include(":piecetable")
project(":piecetable").projectDir = file("modules/piecetable/lib")

// other module
include(
    "model-core", "model-text", "model-edit", "model-buffer", "model-editor")
include("syntax-base")
include("ui-app", "ui-pane")

include("javafx")
include("model-layout", "model-layout-fx")


for (project in rootProject.children.filterNot {
        it.name == "app" || it.name == "piecetable" }) {
    project.projectDir = file("modules/${project.name}")
}
