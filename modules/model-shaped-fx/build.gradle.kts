plugins {
    id("code.editor.base")
}

dependencies {
    implementation(project(":javafx-shim"))
    implementation(project(":model-core"))
    implementation(project(":model-style"))
    implementation(project(":model-layout"))
    implementation(project(":model-shaped"))
}
