plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "min-editor"

include("bootstrap")

// git submodule
include(":piecetable")
project(":piecetable").projectDir = file("modules/piecetable/lib")

// other module
include("model-text", "model-content", "model-edit", "model-buffer", "model-layout", "model-find")
include("syntax", "syntax-base", "syntax-basic", "syntax-java", "syntax-javascript",
    "syntax-rust", "syntax-kotlin",  "syntax-python", "syntax-markdown", "syntax-html",
    "syntax-toml", "syntax-sql")
include("ui-app", "ui-prefs", "ui-model", "ui-pane")

// javaFX
include("javafx", "javafx-peep", "javafx-original")
include("model-layout-fx")


for (project in rootProject.children.filterNot { it.name == "piecetable" }) {
    project.projectDir = file("modules/${project.name}")
}
