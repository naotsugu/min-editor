plugins {
    id("code.editor.base")
}

dependencies {
    implementation(project(":javafx-shim"))
    api(project(":model-layout"))
    api(project(":model-style"))
}
