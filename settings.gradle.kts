plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "min-editor"

include("bootstrap")

// git submodule
include(":piecetable")
project(":piecetable").projectDir = file("modules/piecetable/lib")

// other module
include("model-text", "model-edit", "model-buffer", "model-layout")
include("syntax", "syntax-base", "syntax-basic", "syntax-java", "syntax-javascript",
    "syntax-rust", "syntax-kotlin",  "syntax-python", "syntax-markdown")
include("ui-app", "ui-prefs", "ui-model", "ui-pane", "ui-control")

// javaFX
include("javafx", "javafx-peep", "javafx-original")
include("model-layout-fx")


for (project in rootProject.children.filterNot { it.name == "piecetable" }) {
    project.projectDir = file("modules/${project.name}")
}
