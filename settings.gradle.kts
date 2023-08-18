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
include("model-text", "model-edit", "model-buffer", "model-layout")
include("syntax", "syntax-base", "syntax-java")
include("ui-app", "ui-pane", "ui-control")

include("javafx", "javafx-peep", "javafx-original")

include("model-layout-fx")


for (project in rootProject.children.filterNot {
        it.name == "app" || it.name == "piecetable" }) {
    project.projectDir = file("modules/${project.name}")
}
