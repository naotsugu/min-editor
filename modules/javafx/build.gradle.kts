plugins {
    `java-library`
    id("code.editor.base")
}

evaluationDependsOn(":javafx-base")
evaluationDependsOn(":javafx-graphics")

dependencies {
    api(project(":javafx-base", "archives"))
    api(project(":javafx-graphics", "archives"))
}
