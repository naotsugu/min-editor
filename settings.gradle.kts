plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "min-editor"

include("bootstrap", "core", "ui", "ui-fx", ) // , "ui-swing"

// javaFX
include("javafx", "javafx-peep", "javafx-original", )

// git submodule
include(":piecetable")
project(":piecetable").projectDir = file("modules/piecetable/lib")

for (project in rootProject.children.filterNot { it.name == "piecetable" }) {
    project.projectDir = file("modules/${project.name}")
}

